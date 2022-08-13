package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import fyi.incomeoutcome.salarytaxspend.data.source.TaxSource;
import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.repository.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.TaxSourceRepository;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
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
                if (taxRecord.dueNewTax()){
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
        TaxSource taxSite = taxSourceRepository.findById(1);
        String countryLower = salary.getCountry().toLowerCase();
        String taxSiteUrl = taxSite.getUrlBase() + salary.getCountry().toLowerCase() + taxSite.getUrlEnd();
        taxSiteUrl = taxSiteUrl.replace(" ", "-");
        log.info(String.format("Fetching with site %s for salary %s", taxSiteUrl, salary.getCountry()));
        driver.get(taxSiteUrl);
        if (countryLower.equals("germany")){
            return getGermanTaxPayable(salary);
        }

        WebElement salaryInput = driver.findElement(By.id("j2"));
        JavascriptExecutor jExec = driver;
        String executionScript = String.format("arguments[0].value='%s';", salary.getCompensation());
        jExec.executeScript(executionScript, salaryInput);
        try {
            WebElement cookieConsent = driver.findElementByXPath("/html/body/div[4]/div[2]/div[1]/div[2]/div[2]/button[1]");
            cookieConsent.click();
        } catch (Exception NoSuchElementException){
            log.warn("Cookie consent was not found, likely already accepted.");
        }
        // Need to click this to get results to expand
        WebElement fullPageView = driver.findElementByXPath("//*[@id=\"calc\"]/table[1]/tbody/tr[2]/td/label[2]");
        fullPageView.click();
        String taxPayableLongTableXPath = "//*[@id=\"taxResults\"]/table[1]/tbody/tr[8]/td[1]";
        String taxPayableShortTableXPath = "//*[@id=\"taxResults\"]/table[1]/tbody/tr[4]/td[1]";
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
        WebElement salaryInput = driver.findElement(By.id("g1"));
        JavascriptExecutor jExec = driver;
        String executionScript = String.format("arguments[0].value='%s';", salary.getCompensation());
        jExec.executeScript(executionScript, salaryInput);
        try {
            WebElement cookieConsent = driver.findElementByXPath("/html/body/div[4]/div[2]/div[1]/div[2]/div[2]/button[1]");
            cookieConsent.click();
        } catch (Exception NoSuchElementException){
            log.warn("Cookie consent was not found, likely already accepted.");
        }
        WebElement fullPageView = driver.findElementByXPath("/html/body/div[2]/div/div[2]/div[2]/form/table[1]/tbody/tr[6]/td/label[2]");

        fullPageView.click();
        String netIncomeXPath = "/html/body/div[2]/div/div[2]/div[2]/div[4]/table/tbody/tr[8]/td[1]";
        WebElement netIncomeElement = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(netIncomeXPath)));
        // Germany doesn't have an easy total deductions field to take from so we work from Net Income
        String netIncome = netIncomeElement.getText().replace(",","")
                .replace("€","");
        double netIncomeDouble = Double.parseDouble(netIncome);
        return salary.getCompensation() - netIncomeDouble;
    }
}
