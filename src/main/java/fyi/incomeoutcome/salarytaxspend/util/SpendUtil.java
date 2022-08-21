package fyi.incomeoutcome.salarytaxspend.util;

import fyi.incomeoutcome.salarytaxspend.data.Spend;
import fyi.incomeoutcome.salarytaxspend.data.source.SpendSource;
import fyi.incomeoutcome.salarytaxspend.repository.source.SpendSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SpendUtil {

    @Autowired
    private static SpendSourceRepository spendSourceRepository;
    private static SpendSource spendSource;

    @Value("${spendSiteId}")
    private static long spendSiteId;

    @Value("${oneBedRentKey}")
    private static String oneBedRentKey;
    @Value("${utilitiesKey}")
    private static String utilitiesKey;
    @Value("${internetKey}")
    private static String internetKey;
    @Value("${milkLiterKey}")
    private static String milkLiterKey;
    @Value("${eggsKey}")
    private static String eggsKey;
    @Value("${shopBeerKey}")
    private static String shopBeerKey;
    @Value("${applesKey}")
    private static String applesKey;
    @Value("${bananasKey}")
    private static String bananasKey;
    @Value("${chickenKey}")
    private static String chickenKey;
    @Value("${onionsKey}")
    private static String onionsKey;
    @Value("${potatoesKey}")
    private static String potatoesKey;
    @Value("${riceKe}")
    private static String riceKey;
    @Value("${monthTransportKey}")
    private static String monthTransportKey;
    @Value("${jeansKey}")
    private static String jeansKey;
    @Value("${summerDressKey}")
    private static String summerDressKey;
    @Value("${mealForTwoOutKey}")
    private static String mealForTwoOutKey;
    @Value("${beerOutKey}")
    private static String beerOutKey;
    @Value("${gymKey}")
    private static String gymKey;
    @Value("${cinemaKey}")
    private static String cinemaKey;

    @Value("${oneBedRentLabel}")
    private static String oneBedRentLabel;
    @Value("${utilitiesLabel}")
    private static String utilitiesLabel;
    @Value("${internetLabel}")
    private static String internetLabel;
    @Value("${milkLiterLabel}")
    private static String milkLiterLabel;
    @Value("${eggsLabel}")
    private static String eggsLabel;
    @Value("${shopBeerLabel}")
    private static String shopBeerLabel;
    @Value("${applesLabel}")
    private static String applesLabel;
    @Value("${bananasLabel}")
    private static String bananasLabel;
    @Value("${chickenLabel}")
    private static String chickenLabel;
    @Value("${onionsLabel}")
    private static String onionsLabel;
    @Value("${potatoesLabel}")
    private static String potatoesLabel;
    @Value("${riceLabel}")
    private static String riceLabel;
    @Value("${monthTransportLabel}")
    private static String monthTransportLabel;
    @Value("${jeansLabel}")
    private static String jeansLabel;
    @Value("${summerDressLabel}")
    private static String summerDressLabel;
    @Value("${mealForTwoOutLabel}")
    private static String mealForTwoOutLabel;
    @Value("${beerOutLabel}")
    private static String beerOutLabel;
    @Value("${gymLabel}")
    private static String gymLabel;
    @Value("${cinemaLabel}")
    private static String cinemaLabel;


    public static HashMap<String, Double> getPricesToConvert(Spend spend){
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

    public static HashMap<String, String> getSpendSelector(){
        HashMap<String, String> spendSelector = new HashMap<>();
        spendSource = spendSourceRepository.findById(spendSiteId);

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

    public static ArrayList<Object[]> getSpendTableValues(Spend spend){
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
}
