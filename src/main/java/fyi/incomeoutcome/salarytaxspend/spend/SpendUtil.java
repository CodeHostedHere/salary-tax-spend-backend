package fyi.incomeoutcome.salarytaxspend.spend;

import fyi.incomeoutcome.salarytaxspend.spendsource.SpendSource;
import fyi.incomeoutcome.salarytaxspend.spendsource.SpendSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@PropertySource(value = "classpath:application.properties")
@Component
public class SpendUtil {

    @Autowired
    private SpendSourceRepository spendSourceRepository;

    private SpendSource spendSource;

    @Value("${spendSiteId}")
    private String spendSiteId;


    @Value("${oneBedRentKey}")
    private String oneBedRentKey;
    @Value("${utilitiesKey}")
    private String utilitiesKey;
    @Value("${internetKey}")
    private String internetKey;
    @Value("${milkLiterKey}")
    private String milkLiterKey;
    @Value("${eggsKey}")
    private String eggsKey;
    @Value("${shopBeerKey}")
    private String shopBeerKey;
    @Value("${applesKey}")
    private  String applesKey;
    @Value("${bananasKey}")
    private  String bananasKey;
    @Value("${chickenKey}")
    private  String chickenKey;
    @Value("${onionsKey}")
    private  String onionsKey;
    @Value("${potatoesKey}")
    private  String potatoesKey;
    @Value("${riceKey}")
    private  String riceKey;
    @Value("${monthTransportKey}")
    private  String monthTransportKey;
    @Value("${jeansKey}")
    private  String jeansKey;
    @Value("${summerDressKey}")
    private  String summerDressKey;
    @Value("${mealForTwoOutKey}")
    private  String mealForTwoOutKey;
    @Value("${beerOutKey}")
    private  String beerOutKey;
    @Value("${gymKey}")
    private  String gymKey;
    @Value("${cinemaKey}")
    private  String cinemaKey;

    @Value("${oneBedRentLabel}")
    private  String oneBedRentLabel;
    @Value("${utilitiesLabel}")
    private  String utilitiesLabel;
    @Value("${internetLabel}")
    private  String internetLabel;
    @Value("${milkLiterLabel}")
    private  String milkLiterLabel;
    @Value("${eggsLabel}")
    private  String eggsLabel;
    @Value("${shopBeerLabel}")
    private  String shopBeerLabel;
    @Value("${applesLabel}")
    private  String applesLabel;
    @Value("${bananasLabel}")
    private  String bananasLabel;
    @Value("${chickenLabel}")
    private  String chickenLabel;
    @Value("${onionsLabel}")
    private  String onionsLabel;
    @Value("${potatoesLabel}")
    private  String potatoesLabel;
    @Value("${riceLabel}")
    private  String riceLabel;
    @Value("${monthTransportLabel}")
    private  String monthTransportLabel;
    @Value("${jeansLabel}")
    private  String jeansLabel;
    @Value("${summerDressLabel}")
    private  String summerDressLabel;
    @Value("${mealForTwoOutLabel}")
    private  String mealForTwoOutLabel;
    @Value("${beerOutLabel}")
    private  String beerOutLabel;
    @Value("${gymLabel}")
    private  String gymLabel;
    @Value("${cinemaLabel}")
    private  String cinemaLabel;


    public  HashMap<String, Double> getPricesToConvert(Spend spend){
        HashMap<String, Double> pricesToConvert = new HashMap<>();
        pricesToConvert.put(oneBedRentKey, spend.getOneBedRent());
        pricesToConvert.put(utilitiesKey, spend.getUtilities());
        pricesToConvert.put(internetKey, spend.getInternet());
        pricesToConvert.put(milkLiterKey, spend.getMilkLiter());
        pricesToConvert.put(eggsKey, spend.getEggs());
        pricesToConvert.put(shopBeerKey, spend.getShopBeer());
        pricesToConvert.put(applesKey, spend.getApples());
        pricesToConvert.put(bananasKey, spend.getBananas());
        pricesToConvert.put(chickenKey, spend.getChicken());
        pricesToConvert.put(onionsKey, spend.getOnions());
        pricesToConvert.put(potatoesKey, spend.getPotatoes());
        pricesToConvert.put(riceKey, spend.getRice());
        pricesToConvert.put(monthTransportKey, spend.getMonthTransport());
        pricesToConvert.put(jeansKey, spend.getJeans());
        pricesToConvert.put(gymKey, spend.getGym());
        pricesToConvert.put(summerDressKey, spend.getSummerDress());
        pricesToConvert.put(mealForTwoOutKey, spend.getMealForTwoOut());
        pricesToConvert.put(cinemaKey, spend.getCinema());
        return pricesToConvert;
    }

    public  HashMap<String, String> getSpendSelector(){
        HashMap<String, String> spendSelector = new HashMap<>();
        log.info("-------------"+spendSiteId+"-------------");
        Long l = Long.parseLong(spendSiteId);

        Optional<SpendSource> spendSourceOptional = spendSourceRepository.findById(l);
        if (spendSourceOptional.isPresent()){
            spendSource = spendSourceOptional.get();
        } else {
            log.error("No spend source found for the id of " + spendSiteId);
        }

        spendSelector.put(oneBedRentKey, spendSource.getOneBedRentSelector());
        spendSelector.put(utilitiesKey, spendSource.getUtilitiesSelector());
        spendSelector.put(internetKey, spendSource.getInternetSelector());
        spendSelector.put(milkLiterKey, spendSource.getMilkLiterSelector());
        spendSelector.put(eggsKey, spendSource.getEggsSelector());
        spendSelector.put(shopBeerKey, spendSource.getShopBeerSelector());
        spendSelector.put(applesKey, spendSource.getApplesSelector());
        spendSelector.put(bananasKey, spendSource.getBananasSelector());
        spendSelector.put(chickenKey, spendSource.getChickenSelector());
        spendSelector.put(onionsKey, spendSource.getOnionsSelector());
        spendSelector.put(potatoesKey, spendSource.getPotatoesSelector());
        spendSelector.put(riceKey, spendSource.getRiceSelector());
        spendSelector.put(monthTransportKey, spendSource.getMonthTransportSelector());
        spendSelector.put(jeansKey, spendSource.getJeansSelector());
        spendSelector.put(summerDressKey, spendSource.getSummerDressSelector());
        spendSelector.put(mealForTwoOutKey, spendSource.getMealForTwoOutSelector());
        spendSelector.put(beerOutKey, spendSource.getBeerOutSelector());
        spendSelector.put(gymKey, spendSource.getGymSelector());
        spendSelector.put(cinemaKey, spendSource.getCinemaSelector());
        return spendSelector;
    }

    public  ArrayList<Object[]> getSpendTableValues(Spend spend){
        // A victim of not having an ExpenseItem object with its own logic
        ArrayList<Object[]> rowList = new ArrayList<>();
        rowList.add(new Object[]{oneBedRentLabel, spend.getOneBedRentConverted(), spend.getOneBedRentQty()});
        rowList.add(new Object[]{utilitiesLabel, spend.getUtilitiesConverted(), spend.getUtilitiesQty()});
        rowList.add(new Object[]{internetLabel, spend.getInternetConverted(), spend.getInternetQty()});
        rowList.add(new Object[]{milkLiterLabel, spend.getMilkLiterConverted(), spend.getMilkQty()});
        rowList.add(new Object[]{eggsLabel, spend.getEggsConverted(), spend.getEggsQty()});
        rowList.add(new Object[]{shopBeerLabel, spend.getShopBeerConverted(), spend.getShopBeerQty()});
        rowList.add(new Object[]{applesLabel, spend.getApplesConverted(), spend.getApplesQty()});
        rowList.add(new Object[]{bananasLabel, spend.getBananasConverted(), spend.getBananasQty()});
        rowList.add(new Object[]{chickenLabel, spend.getChickenConverted(), spend.getChickenQty()});
        rowList.add(new Object[]{onionsLabel, spend.getOnionsConverted(), spend.getOnionsQty()});
        rowList.add(new Object[]{potatoesLabel, spend.getPotatoesConverted(), spend.getPotatoesQty()});
        rowList.add(new Object[]{riceLabel, spend.getRiceConverted(), spend.getRiceQty()});
        rowList.add(new Object[]{monthTransportLabel, spend.getMonthTransportConverted(), spend.getMonthTransportQty()});
        rowList.add(new Object[]{jeansLabel, spend.getJeansConverted(), spend.getJeansQty()});
        rowList.add(new Object[]{summerDressLabel, spend.getSummerDressConverted(), spend.getSummerDressQty()});
        rowList.add(new Object[]{mealForTwoOutLabel, spend.getMealForTwoOutConverted(), spend.getMealForTwoOutQty()});
        rowList.add(new Object[]{beerOutLabel, spend.getBeerOutConverted(), spend.getBeerOutQty()});
        rowList.add(new Object[]{gymLabel, spend.getGymConverted(), spend.getGymQty()});
        rowList.add(new Object[]{cinemaLabel, spend.getCinemaConverted(), spend.getCinemaQty()});

        return rowList;
    }

    public boolean dueNewSpend(Spend spend, long currentTime){
        long lastUpdated = spend.getUpdatedOn().getTime();
        long daysSinceLastUpdate = TimeUnit.DAYS.convert(currentTime-lastUpdated, TimeUnit.MILLISECONDS);
        return daysSinceLastUpdate > 365;
    }
}
