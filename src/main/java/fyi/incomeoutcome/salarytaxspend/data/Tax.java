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
