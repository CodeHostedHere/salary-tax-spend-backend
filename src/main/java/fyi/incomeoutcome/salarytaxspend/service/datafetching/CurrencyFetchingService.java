package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.data.Spend;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import fyi.incomeoutcome.salarytaxspend.data.source.CurrencySource;
import fyi.incomeoutcome.salarytaxspend.repository.CurrencyRepository;
import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.repository.SpendRepository;
import fyi.incomeoutcome.salarytaxspend.repository.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.CurrencySourceRepository;
import fyi.incomeoutcome.salarytaxspend.data.Currency;
import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.util.SpendUtil;
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
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CurrencyFetchingService implements DataFetchingService {
    @Autowired
    private CurrencySourceRepository currencySourceRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private SalaryRepository salaryRepository;
    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private SpendRepository spendRepository;

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

    @Override
    public void refreshAll(){
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        refreshAllSalaries(today);
        refreshAllTax(today);
        refreshAllSpend(today);
    }

    private void refreshAllSpend(Date today) {
        List<Spend> spends = spendRepository.getRequiringConversion(today);
        for (Spend spend: spends){
            setConvertedSpend(spend, today);
        }
    }

    private void setConvertedSpend(Spend spend, java.sql.Date today){
        HashMap<String, Double> pricesToConvert = SpendUtil.getPricesToConvert(spend);
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
        spend.setConvertedOn(today);
        spendRepository.save(spend);
    }

    /*
       The functions below should be made more generic to stop repetition
     */
    public void refreshAllSalaries(java.sql.Date today){
        List<Salary> salaries = salaryRepository.getRequiringConversion(today);
        for (Salary sal : salaries){
            double convertedSalary = convertSalaryToEuro(sal);
            sal.setCompensationConverted(convertedSalary);
            log.info(String.format("Saving salary %s with converted salary of %7.2f", sal,convertedSalary));
            salaryRepository.save(sal);
        }
    }

    public void refreshAllTax(java.sql.Date today){
        List<Tax> taxRecords = taxRepository.getRequiringConversion(today);
        for (Tax tax : taxRecords){
            double convertedTax = convertTaxToEuro(tax);
            log.info(String.format("Saving tax %s with converted salary of %7.2f", tax, convertedTax));
            tax.setTaxPayableConverted(convertedTax);
            taxRepository.save(tax);
        }
    }

    public double convertSalaryToEuro(Salary salary){
        String currencyUsed = salary.getCurrency();
        double compensation = salary.getCompensation();
        return convertFigureToEuro(compensation, currencyUsed);
    }

    public double convertTaxToEuro(Tax tax){
        double taxPayable = tax.getTaxPayable();
        String currencyUsed = tax.getCurrencyUsed();
        return convertFigureToEuro(taxPayable, currencyUsed);
    }

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
        return figure / currencyRate;
    }

    public void saveCurrencyRates(){
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
}
