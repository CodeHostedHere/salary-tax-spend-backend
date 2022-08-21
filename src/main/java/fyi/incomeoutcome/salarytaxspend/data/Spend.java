package fyi.incomeoutcome.salarytaxspend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Spend {

    // Spend shouldn’t have hardcoded string representations. Should have broken 'Spend' in to ‘ExpenseItem’ objects with
    // string representations for internationalisation. Current form has fields, Converted fields and Qty fields
    // without a coded relationship to confirm their logical relationship.

    // Spend should probably be named something more informative, like ExpenseList

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REMOVE,
            CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "cityId")
    private City city;
    private String currency;
    private java.sql.Date updatedOn;
    private java.sql.Date convertedOn;

    // Base Prices
    private double oneBedRent;
    private double utilities;
    private double internet;

    private double milkLiter;
    private double eggs;
    private double shopBeer;
    private double apples;
    private double bananas;
    private double chicken;
    private double onions;
    private double potatoes;
    private double rice;

    private double monthTransport;

    private double jeans;
    private double summerDress;

    private double mealForTwoOut;
    private double beerOut;

    private double gym;
    private double cinema;

    // Converted Prices
    private double oneBedRentConverted;
    private double utilitiesConverted;
    private double internetConverted;

    private double milkLiterConverted;
    private double eggsConverted;
    private double shopBeerConverted;
    private double applesConverted;
    private double bananasConverted;
    private double chickenConverted;
    private double onionsConverted;
    private double potatoesConverted;
    private double riceConverted;

    private double monthTransportConverted;

    private double jeansConverted;
    private double summerDressConverted;

    private double mealForTwoOutConverted;
    private double beerOutConverted;

    private double gymConverted;
    private double cinemaConverted;

    // Quantities
    private double oneBedRentQty;
    private double utilitiesQty;
    private double internetQty;

    private double milkQty;
    private double eggsQty;
    private double shopBeerQty;
    private double applesQty;
    private double bananasQty;
    private double chickenQty;
    private double onionsQty;
    private double potatoesQty;
    private double riceQty;

    private double monthTransportQty;

    private double jeansQty;
    private double summerDressQty;

    private double mealForTwoOutQty;
    private double beerOutQty;

    private double gymQty;
    private double cinemaQty;

    public boolean dueNewSpend(){
        Long today = System.currentTimeMillis();
        Long lastUpdated = this.updatedOn.getTime();
        long daysSinceLastUpdate = TimeUnit.DAYS.convert(today-lastUpdated, TimeUnit.MILLISECONDS);
        return daysSinceLastUpdate > 365;
    }



}
