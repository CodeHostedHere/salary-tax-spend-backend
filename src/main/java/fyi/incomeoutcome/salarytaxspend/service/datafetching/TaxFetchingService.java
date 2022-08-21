package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import fyi.incomeoutcome.salarytaxspend.data.source.TaxSource;
import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.repository.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.TaxSourceRepository;

import fyi.incomeoutcome.salarytaxspend.util.TaxUtil;
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

@Slf4j
@Component
public class TaxFetchingService implements DataFetchingService {
    @Autowired
    private TaxSourceRepository taxSourceRepository;
    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private SalaryRepository salaryRepository;
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

    public void setupDriver(){
        if (this.driver == null){
            WebDriverManager.chromedriver().setup();
            this.driver = new ChromeDriver();
        }
    }

    public void refreshAll(){
        List<Salary> salaryList =  new ArrayList<>();
        salaryRepository.findAll().forEach(salaryList::add);
        for (Salary salary: salaryList){
            var taxRecordOptional = taxRepository.findBySalary(salary);
            boolean taxRequired = false;
            if (taxRecordOptional.isPresent()){
                Tax taxRecord = taxRecordOptional.get();
                if (TaxUtil.dueNewTax(taxRecord)){
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

    public double getTaxPayable(Salary salary){
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

    public double getGermanTaxPayable(Salary salary){
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
