package fyi.incomeoutcome.salarytaxspend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Entity
public class Tax {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;
    private double taxPayable;
    private double taxPayableConverted;
    @Basic
    private java.sql.Date updatedOn;
    @Basic
    private java.sql.Date convertedOn;

    protected Tax(){}

    public Tax(Salary salary, double taxPayable){
        this.salary = salary;
        this.taxPayable = taxPayable;
        this.taxPayableConverted = 0.0;
        this.updatedOn = new java.sql.Date(System.currentTimeMillis());
        this.convertedOn = null;
    }

    public boolean dueNewTax(){
        Salary relatedSalary = this.getSalary();
        long compensationLastUpdated = relatedSalary.getUpdatedOn().getTime();
        long taxLastUpdated = this.getUpdatedOn().getTime();
        long today = System.currentTimeMillis();
        long daysSinceLastTaxUpdate = TimeUnit.DAYS.convert(today-taxLastUpdated, TimeUnit.MILLISECONDS);
        boolean taxNeverUpdated = taxLastUpdated == 0;
        boolean taxIsForOldCompenstion = compensationLastUpdated > taxLastUpdated;
        boolean taxIsForLastYear = daysSinceLastTaxUpdate > 365;
        log.debug(String.format("For country %s, taxNeverUpdated %b, taxIsForOldCompensation %b ,taxIsForLastYear %b",
                relatedSalary.getCountry(), taxNeverUpdated, taxIsForOldCompenstion, taxIsForLastYear));
        return (taxNeverUpdated || taxIsForOldCompenstion || taxIsForLastYear );
    }

    @JsonIgnore
    public java.sql.Date getUpdatedOn(){ return this.updatedOn; }

    public double getTaxPayable() { return this.taxPayable; }

    public double getTaxPayableConverted() { return this.taxPayableConverted; }

    public Salary getSalary(){
        return this.salary;
    }

    public String getCurrencyUsed() {
        return this.salary.getCurrency();
    }

    public void setTaxPayableConverted(double taxPayableConverted){
        this.taxPayableConverted = taxPayableConverted;
        this.convertedOn = new java.sql.Date(System.currentTimeMillis());
    }
}
