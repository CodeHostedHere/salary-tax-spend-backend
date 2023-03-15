package fyi.incomeoutcome.salarytaxspend.currency;

import fyi.incomeoutcome.salarytaxspend.spend.Spend;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import fyi.incomeoutcome.salarytaxspend.salary.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.spend.SpendRepository;
import fyi.incomeoutcome.salarytaxspend.tax.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.spend.SpendUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  The Data fetching service which gets currency rates and implements conversions
 */
@Slf4j
@Component
public class CurrencyFetchingService {
    private CurrencySourceRepository currencySourceRepository;
    private CurrencyRepository currencyRepository;
    private SalaryRepository salaryRepository;
    private TaxRepository taxRepository;
    private SpendRepository spendRepository;
    private SpendUtil spendUtil;

    @Value("${currencySiteId}")
    private long currencySiteId;
    @Value("${localCurrencySymbolRepresentation}")
    private String localCurrencySymbolRepresentation;
    @Value("${localCurrencyCodeRepresentation}")
    private String localCurrencyCodeRepresentation;
    @Value("${currencyCodeKey}")
    private String currencyCodeKey;
    @Value("${currencyValueKey}")
    private String currencyValueKey;

    public CurrencyFetchingService(){}

    @Autowired
    public CurrencyFetchingService(CurrencySourceRepository currencySourceRepository,
                                   CurrencyRepository currencyRepository, SalaryRepository salaryRepository,
                                   TaxRepository taxRepository, SpendRepository spendRepository, SpendUtil spendUtil){
        this.currencySourceRepository = currencySourceRepository;
        this.currencyRepository = currencyRepository;
        this.salaryRepository = salaryRepository;
        this.taxRepository = taxRepository;
        this.spendRepository = spendRepository;
        this.spendUtil = spendUtil;
    }

    /**
     *  Checks through all Salary, Tax, and Spend database items to see if it is due a new currency conversion,
     *  due to lack of one or time passed since last conversion rendering the current conversion inaccurate.
     *
     *   @param currentTime time when function is called, used to compare against older records insertion time
     */
    public void refreshAll(long currentTime){
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        refreshAllSalaries(currentTime);
        refreshAllTax(currentTime);
        refreshAllSpend(currentTime);
    }

    /**
     *  Checks through the Spend items in the database to see what requires new conversion and carries out updates.
     *
     *   @param currentTime time when function is called, used to compare against older records insertion time
     */
    public void refreshAllSpend(long currentTime) {
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        List<Spend> spends = spendRepository.getRequiringConversion(currentDateTime);
        for (Spend spend: spends){
            setConvertedSpend(spend, currentTime);
        }
    }

    /**
     *  Checks through the Salary items in the database to see what requires new conversion and carries out updates.
     *
     *   @param currentTime time when function is called, used to compare against older records insertion time
     */
    public void refreshAllSalaries(long currentTime){
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        List<Salary> salaries = salaryRepository.getRequiringConversion(currentDateTime);

        for (Salary sal : salaries){
            double convertedSalary = convertSalaryToEuro(sal);
            sal.setCompensationConverted(convertedSalary);
            log.info(String.format("Saving salary %s with converted salary of %7.2f", sal, convertedSalary));
            salaryRepository.save(sal);
        }
    }

    /**
     *  Checks through the Tax items in the database to see what requires new conversion and carries out updates.
     *
     *   @param currentTime time when function is called, used to compare against older records insertion time
     */
    public void refreshAllTax(long currentTime){
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        List<Tax> taxRecords = taxRepository.getRequiringConversion(currentDateTime);
        for (Tax tax : taxRecords){
            double convertedTax = convertTaxToEuro(tax);
            log.info(String.format("Saving tax %s with converted salary of %7.2f", tax, convertedTax));
            tax.setTaxPayableConverted(convertedTax);
            taxRepository.save(tax);
        }
    }

    /**
     *  Takes a salary object and converts to euro
     *
     * @param salary the salary object that requires conversion to euro
     * @return double of given salary represented in euro
     */
    public double convertSalaryToEuro(Salary salary){
        String currencyUsed = salary.getCurrency();
        double compensation = salary.getCompensation();
        return convertFigureToEuro(compensation, currencyUsed);
    }

    /**
     *  Takes a tax object and converts to euro
     *
     * @param tax the tax object that requires conversion to euro
     * @return double of given tax represented in euro
     */
    public double convertTaxToEuro(Tax tax){
        double taxPayable = tax.getTaxPayable();
        String currencyUsed = tax.getCurrencyUsed();
        return convertFigureToEuro(taxPayable, currencyUsed);
    }

    /**
     *  Takes a double and converts to euro. Will save currency rates if they are not present
     *  Used by other conversion methods.
     *
     * @param figure the double that requires conversion to euro
     * @param currencyUsed the currency code from which to convert the figure param
     * @return double of given tax represented in euro
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public double convertFigureToEuro(double figure, String currencyUsed){
        if (currencyUsed.equals(localCurrencySymbolRepresentation)){
            return figure;
        }
        var currencyNeededOptional = currencyRepository.findByCurrencyCode(currencyUsed);
        if (currencyNeededOptional.isEmpty()){
            saveCurrencyRates();
            currencyNeededOptional = currencyRepository.findByCurrencyCode(currencyUsed);
        }
        Currency currencyNeeded = currencyNeededOptional.get();
        double currencyRate = currencyNeeded.getConversionRate();
        BigDecimal bd = BigDecimal.valueOf(figure / currencyRate);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void saveCurrencyRates(){
        long currencySiteId = 1;
        CurrencySource currencySite = currencySourceRepository.findById(currencySiteId);
        String dataKey = currencySite.getJsonResultDataKey();
        String currencySiteUrl = currencySite.getCurrencyUrl();
        String currencyResults = "NA";
        try {
            currencyResults = Jsoup.connect(currencySiteUrl).ignoreContentType(true).get().body().text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject currencyResultsJson = new JSONObject(currencyResults).getJSONObject(dataKey);
        Iterator<String> keys = currencyResultsJson.keys();
        while(keys.hasNext()){
            String key = keys.next();
            log.info(String.valueOf(currencyResultsJson.get(key)));
            JSONObject currencyEntry = (JSONObject) currencyResultsJson.get(key);
            log.info(String.valueOf(currencyEntry));
            String currencyCode = (String) currencyEntry.get(currencyCodeKey);
            if (currencyCode.equals(localCurrencyCodeRepresentation)){
                continue;
            }
            BigDecimal currencyRate = (BigDecimal) currencyEntry.get(currencyValueKey);
            double currencyRateDouble = currencyRate.doubleValue();
            log.info(String.format("Currency code, rate : %s %s", currencyCode, currencyRateDouble));
            Currency generatedCurrency = new Currency(currencyCode, currencyRateDouble);
            currencyRepository.save(generatedCurrency);
        }
    }

    private void setConvertedSpend(Spend spend, long currentTime){
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        HashMap<String, Double> pricesToConvert = spendUtil.getPricesToConvert(spend);
        String currencyUsed = spend.getCurrency();
        log.info("Starting conversion of spend for city " + spend.getCity());
        for(Map.Entry<String,Double> priceToConvert: pricesToConvert.entrySet()){
            try {
                double convertedPrice = convertFigureToEuro(priceToConvert.getValue(), currencyUsed);
                String convertedPropertyName = priceToConvert.getKey() + "Converted";
                log.info(String.format("Saving key %s with value %7.2f as converted value %7.2f",
                        convertedPropertyName, priceToConvert.getValue(), convertedPrice));
                BeanUtils.setProperty(spend, convertedPropertyName, convertedPrice);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("Saving spend for city %s", spend.getCity()));
        spend.setConvertedOn(currentDateTime);
        spendRepository.save(spend);
    }
}
