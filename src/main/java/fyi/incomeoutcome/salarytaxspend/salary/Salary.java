package fyi.incomeoutcome.salarytaxspend.salary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySource;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import javax.persistence.*;

@Slf4j
@NoArgsConstructor
@Entity
public class  Salary {

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

    @Value("${taxNeverSetDate}")
    private String taxNeverSetDate;

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

    public void setCompensationConverted(double currencyConverted){
        this.compensationConverted = currencyConverted;
        this.convertedOn = new java.sql.Date(System.currentTimeMillis());
    }

    public String getCountry() { return city.getCountry(); }

    public City getCity(){
        return city;
    }

    public Role getRole() { return this.role;}

    public double getCompensation(){
        return this.compensation;
    }

    public double getCompensationConverted(){ return this.compensationConverted; }

    public String getCurrency() { return this.currency.trim(); }

    public long getId() {return this.id; }

    @JsonIgnore
    public java.sql.Date getUpdatedOn(){ return this.updatedOn; }

    public String toNewString() {
        String stringRepresentation = this.role.toString() + " " + this.city
                + " " +String.valueOf(this.compensation);
        return stringRepresentation;
    }

    public SalarySource getSalarySource() { return this.getSalarySource(); }
}
