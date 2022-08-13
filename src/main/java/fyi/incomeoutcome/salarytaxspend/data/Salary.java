package fyi.incomeoutcome.salarytaxspend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fyi.incomeoutcome.salarytaxspend.data.source.SalarySource;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
//import java.sql.Date;
@Slf4j
@ToString
@NoArgsConstructor
@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private double compensation;
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;
    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne(optional = false)
    @JoinColumn(name = "site_id")
    private SalarySource source;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="salary")
    private Tax tax;
    private String currency;
    private double compensationConverted;
    @Basic
    private java.sql.Date updatedOn;
    @Basic
    private java.sql.Date convertedOn;

    public Salary(int compensation, Role role, City city, SalarySource source, String currency){
        this.compensation = compensation;
        this.role = role;
        this.city = city;
        this.source = source;
        this.currency = currency;
        this.updatedOn = new java.sql.Date(System.currentTimeMillis());
        this.compensationConverted = 0;
        this.convertedOn = java.sql.Date.valueOf("1970-01-01");
    }

    public boolean dueNewCompensation(){
        if (this.updatedOn == null){
            return true;
        }
        Date lastUpdated = new java.util.Date(this.updatedOn.getTime());
        Date today = new Date(System.currentTimeMillis());
        long diffInMs = today.getTime() - lastUpdated.getTime();
        long daysSinceLastConversion = TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
        return (daysSinceLastConversion > 183);
    }

    public void setCompensationConverted(double currencyConverted){
        this.compensationConverted = currencyConverted;
        this.convertedOn = new java.sql.Date(System.currentTimeMillis());
    }

    public String getCountry() { return city.getCountry(); }

    public String getCity(){
        return city.getName();
    }

    public double getCompensation(){
        return this.compensation;
    }

    public double getCompensationConverted(){ return this.compensationConverted; }

    public String getCurrency() { return this.currency.trim(); }

    public long getId() {return this.id; }

    @JsonIgnore
    public java.sql.Date getUpdatedOn(){ return this.updatedOn; }

}
