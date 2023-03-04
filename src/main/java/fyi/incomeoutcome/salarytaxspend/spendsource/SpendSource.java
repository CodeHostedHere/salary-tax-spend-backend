package fyi.incomeoutcome.salarytaxspend.spendsource;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class SpendSource {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;
    private String urlBase;

    private String oneBedRentSelector;
    private String utilitiesSelector;
    private String internetSelector;

    private String milkLiterSelector;
    private String eggsSelector;
    private String shopBeerSelector;
    private String applesSelector;
    private String bananasSelector;
    private String chickenSelector;
    private String onionsSelector;
    private String potatoesSelector;
    private String riceSelector;
    private String monthTransportSelector;

    private String jeansSelector;
    private String summerDressSelector;

    private String mealForTwoOutSelector;
    private String beerOutSelector;

    private String gymSelector;
    private String cinemaSelector;

    public String getUrlBase(){ return this.urlBase; }

}
