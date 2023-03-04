package fyi.incomeoutcome.salarytaxspend.tax;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.service.datafetching.DataFetchingService;
import fyi.incomeoutcome.salarytaxspend.salary.SalaryRepository;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Using one website for tax calculation, gets the tax associated with a salary in a city
 * This class should use a webscraper with rendered javascript for speed and removal of UI dependencies,
 * but it uses Selenium instead.
 * This is to gain familiarity with this testing library.
 *
 * @author glacey
 */
@Slf4j
@Component
public class TaxFetchingService implements DataFetchingService {

    private TaxSourceRepository taxSourceRepository;
    private TaxRepository taxRepository;
    private SalaryRepository salaryRepository;
    private TaxUtil taxUtil;

    @Autowired
    public TaxFetchingService(TaxSourceRepository taxSourceRepository, TaxRepository taxRepository,
                              SalaryRepository salaryRepository, TaxUtil taxUtil){

        this.taxSourceRepository = taxSourceRepository;
        this.taxRepository = taxRepository;
        this.salaryRepository = salaryRepository;
        this.taxUtil = taxUtil;
    }

    private ChromeDriver driver;

    @Value("${taxSourceId}")
    private long taxSourceId;
    @Value("${taxUrlDelimiter}")
    private String taxUrlDelimiter;
    @Value("${unusualCountryTaxPage}")
    private String unusualCountryTaxPage;
    @Value("${salaryInputId}")
    private String salaryInputId;
    @Value("${jsEnterValueScript}")
    private String jsEnterValueScript;
    @Value("${cookieConsentXPath}")
    private String cookieConsentXPath;
    @Value("${fullPageViewXPath}")
    private String fullPageViewXPath;
    @Value("${taxPayableLongTableXPath}")
    private String taxPayableLongTableXPath;
    @Value("${taxPayableShortTableXPath}")
    private String taxPayableShortTableXPath;
    @Value("${germanSalaryInput}")
    private String germanSalaryInput;
    @Value("germanFullPageViewXPath")
    private String germanFullPageViewXPath;
    @Value("germanNetIncomeXPath")
    private String germanNetIncomeXPath;
    @Value("${localCurrencySymbolRepresentation}")
    private String localCurrencySymbolRepresentation;

    public TaxFetchingService(){
    }

    /**
     * Completes setup required to run a chromedriver using WebDriverManager library
     */
    private void setupDriver(){
        if (this.driver == null){
            WebDriverManager.chromedriver().setup();
            this.driver = new ChromeDriver();
        }
    }

    /**
     * Fetches each salary from the database and checks if it has a tax record, or if its tax record has expired.
     *
     * @param currentTime time function is called in milliseconds
     */
    public void refreshAll(long currentTime){
        List<Salary> salaryList =  new ArrayList<>();
        salaryRepository.findAll().forEach(salaryList::add);
        // Todo: Compare vs large query of tax records with salary ids, to see if they are present +
        //       query of salary records with records due for conversion
        for (Salary salary: salaryList){
            var taxRecordOptional = taxRepository.findBySalary(salary);
            boolean taxRequired = false;
            if (taxRecordOptional.isPresent()){
                Tax taxRecord = taxRecordOptional.get();
                if (taxUtil.dueNewTax(taxRecord, currentTime)){
                    log.debug(String.format("Tax for salary %s out of date", salary));
                    taxRequired = true;
                }
            } else {
                log.debug(String.format("Tax for salary %s not found", salary));
                taxRequired = true;
            }
            if (taxRequired){
                double taxPayable = getTaxPayable(salary);
                Tax calculatedTax = new Tax(salary, taxPayable);
                taxRepository.save(calculatedTax);
            }
        }
    }

    /**
     * Runs a chrome webdriver to interact with the tax source site, enter required details and scrape the new salary
     *
     * @param salary a salary object which requires a new tax record
     * @return double of the tax payable on the salary
     */
    private double getTaxPayable(Salary salary){
        setupDriver();
        TaxSource taxSite = taxSourceRepository.findById(taxSourceId);
        String countryLower = salary.getCountry().toLowerCase();
        String taxSiteUrl = taxSite.getUrlBase() + salary.getCountry().toLowerCase() + taxSite.getUrlEnd();
        taxSiteUrl = taxSiteUrl.replace(" ", taxUrlDelimiter);
        log.info(String.format("Fetching with site %s for salary %s", taxSiteUrl, salary.getCountry()));
        driver.get(taxSiteUrl);
        if (countryLower.equals(unusualCountryTaxPage)){
            return getGermanTaxPayable(salary);
        }

        WebElement salaryInput = driver.findElement(By.id(salaryInputId));
        JavascriptExecutor jExec = driver;
        String executionScript = String.format(jsEnterValueScript, salary.getCompensation());
        jExec.executeScript(executionScript, salaryInput);
        try {
            WebElement cookieConsent = driver.findElementByXPath(cookieConsentXPath);
            cookieConsent.click();
        } catch (Exception NoSuchElementException){
            log.warn("Cookie consent was not found, likely already accepted.");
        }
        // Need to click this to get results to expand
        WebElement fullPageView = driver.findElementByXPath(fullPageViewXPath);
        fullPageView.click();
        String taxPayable;
        try {
            WebElement taxPayableElement = new WebDriverWait(driver, 5)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(taxPayableLongTableXPath)));
            taxPayable = taxPayableElement.getText().replace(",","");
        } catch (Exception NoSucElementException){
            WebElement taxPayableElement = new WebDriverWait(driver, 5)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(taxPayableShortTableXPath)));
            taxPayable = taxPayableElement.findElement(By.xpath(taxPayableShortTableXPath)).getText().replace(",","");
        }
        double taxPayableDouble = Double.parseDouble(taxPayable);
        log.info(String.format("For %s Tax payable is : %4.2f", salary, taxPayableDouble));
        return taxPayableDouble;
    }

    private double getGermanTaxPayable(Salary salary){
        WebElement salaryInput = driver.findElement(By.id(germanSalaryInput));
        JavascriptExecutor jExec = driver;
        String executionScript = String.format(jsEnterValueScript, salary.getCompensation());
        jExec.executeScript(executionScript, salaryInput);
        try {
            WebElement cookieConsent = driver.findElementByXPath(cookieConsentXPath);
            cookieConsent.click();
        } catch (Exception NoSuchElementException){
            log.warn("Cookie consent was not found, likely already accepted.");
        }
        WebElement fullPageView = driver.findElementByXPath(germanFullPageViewXPath);

        fullPageView.click();
        WebElement netIncomeElement = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(germanNetIncomeXPath)));
        // Germany doesn't have an easy total deductions field to take from so we work from Net Income
        String netIncome = netIncomeElement.getText().replace(",","")
                .replace(localCurrencySymbolRepresentation,"");
        double netIncomeDouble = Double.parseDouble(netIncome);
        return salary.getCompensation() - netIncomeDouble;
    }
}
