package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.data.City;
import fyi.incomeoutcome.salarytaxspend.data.Spend;
import fyi.incomeoutcome.salarytaxspend.data.source.SpendSource;
import fyi.incomeoutcome.salarytaxspend.repository.CityRepository;
import fyi.incomeoutcome.salarytaxspend.repository.SpendRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.SpendSourceRepository;

import fyi.incomeoutcome.salarytaxspend.util.SpendUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
@Component
public class SpendFetchingService implements DataFetchingService {

    private CityRepository cityRepository;
    private SpendRepository spendRepository;
    private SpendSourceRepository spendSourceRepository;
    private SpendUtil spendUtil;

    private SpendSource spendSource;
    private HashMap<String, String> spendSelector = new HashMap<>();

    @Value("${spendDefaultCurrencyCode}")
    private String spendDefaultCurrencyCode;
    @Value("${spendDefaultCurrencySymbol}")
    private String spendDefaultCurrencySymbol;
    @Value("${spendSiteId}")
    private long spendSiteId;

    public SpendFetchingService() {
    }

    @Autowired
    public SpendFetchingService(CityRepository cityRepository, SpendRepository spendRepository,
                                SpendSourceRepository spendSourceRepository, SpendUtil spendUtil,
                                SpendSource spendSource) {
        this.cityRepository = cityRepository;
        this.spendRepository = spendRepository;
        this.spendSourceRepository = spendSourceRepository;
        this.spendUtil = spendUtil;
        this.spendSource = spendSource;
    }

    @PostConstruct
    public void init(){
        spendSelector = spendUtil.getSpendSelector();
    }

    public void refreshAll(long currentTime){
        ArrayList<City> cityList = new ArrayList<>(cityRepository.findAll());
        for (City city: cityList){
            var spendRecordOptional = spendRepository.findByCity(city);
            boolean requiresUpdate = true;
            if (spendRecordOptional.isPresent()){
                Spend spend = spendRecordOptional.get();
                requiresUpdate = spendUtil.dueNewSpend(spend, currentTime);
            } else {
                log.info(String.format("Spend for City %s not found", city));
            }
            if (requiresUpdate){
                log.info(String.format("Spend for City %s requires update", city));
                fetchAndSaveSpend(city);
            }
        }
    }

    public void fetchAndSaveSpend(City city){
        String urlBase = SpendFetchingService.this.spendSource.getUrlBase();
        String cityName = city.getName().toLowerCase(Locale.ROOT);
        String urlFull = urlBase + cityName;
        HashMap<String, Double> spendPrices = new HashMap<>();

        try {
            Document spendPage = Jsoup.connect(urlFull).get();
            log.info(String.format("Connect to page for url %s", urlFull));
            spendPrices = parseSpends(spendPage);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        Spend spend = Spend.builder()
                .city(city)
                .currency(spendDefaultCurrencyCode)
                .updatedOn(today)
                .build();
        buildSpend(spend, spendPrices);
        spendRepository.save(spend);
    }

    private HashMap<String, Double> parseSpends(Document spendPage){
        HashMap<String, Double> spendPrices = new HashMap<>();
        spendSelector.forEach(
                (key, value) -> spendPrices.put(key, parsePrice(value, key, spendPage)));
        return spendPrices;
    }

    private double parsePrice(String value, String key, Document spendPage){
        double itemPriceDouble = 0.0;
        try {
            String itemPrice = spendPage.select(value).first().text();
            itemPrice = itemPrice.replace(spendDefaultCurrencySymbol, "").replace(",", "");
            itemPriceDouble = Double.parseDouble(itemPrice);
        } catch (NumberFormatException | NullPointerException e) {
            log.error("Price not found for " + key);
        }
        return itemPriceDouble;
    }

    private void buildSpend(Spend spend, HashMap<String,Double> spendPrices){
        for (Map.Entry<String, Double> mapElement : spendPrices.entrySet()){
            String key = mapElement.getKey();
            double value = mapElement.getValue();
            try {
                //log.info(String.format("Attempting to save %s as %1$,.2f", key, value));
                log.info(String.format("Saving %s as %f", key, value));
                if (value <= 0.0){
                    throw new IllegalArgumentException("Prices must be greater than 0");
                }
                BeanUtils.setProperty(spend, key, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
