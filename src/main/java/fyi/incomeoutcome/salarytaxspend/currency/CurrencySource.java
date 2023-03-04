package fyi.incomeoutcome.salarytaxspend.currency;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CurrencySource {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;
    private String currencyUrl;
    private String jsonResultDataKey;

    protected CurrencySource(){}

    public CurrencySource(String name, String currencyUrl){
        this.name = name;
        this.currencyUrl = currencyUrl;
    }

    public String getCurrencyUrl(){ return this.currencyUrl; }

    public String getJsonResultDataKey(){ return this.jsonResultDataKey; }
}
