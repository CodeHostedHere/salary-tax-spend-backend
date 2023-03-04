package fyi.incomeoutcome.salarytaxspend.currency;

import javax.persistence.*;

@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String currencyCode;
    private double conversionRate;
    @Basic
    private java.sql.Date updatedOn;

    protected Currency(){}

    public Currency(String currencyCode, double conversionRate){
        this.currencyCode = currencyCode;
        this.conversionRate = conversionRate;
        this.updatedOn = new java.sql.Date(System.currentTimeMillis());
    }

    public double getConversionRate() { return this.conversionRate; }

}
