package fyi.incomeoutcome.salarytaxspend.salary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import javax.persistence.*;
import java.util.Objects;

@Slf4j
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
    private SalarySource salarySource;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="salary")
    private Tax tax;
    private String currency;
    private double compensationConverted;
    @Basic
    private java.sql.Date updatedOn;
    @Basic
    private java.sql.Date convertedOn;

    //@Value("${taxNeverSetDate}")
   // private String taxNeverSetDate;

    public Salary(int compensation, Role role, City city, SalarySource source, String currency){
        this.compensation = compensation;
        this.role = role;
        this.city = city;
        this.salarySource = source;
        this.currency = currency;
        this.updatedOn = new java.sql.Date(System.currentTimeMillis());
        this.compensationConverted = 0;
        this.convertedOn = java.sql.Date.valueOf("1970-01-01");
    }

    public Salary(Role role, City city, SalarySource salarySource){
        this.role = role;
        this.city = city;
        this.salarySource = salarySource;
        this.currency = "";
        this.compensation = 0;
        this.compensationConverted = 0;
        this.updatedOn = java.sql.Date.valueOf("1970-01-01");
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

    public SalarySource getSalarySource() { return this.salarySource; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salary salary = (Salary) o;
        return Objects.equals(role, salary.role) && Objects.equals(city, salary.city) && Objects.equals(salarySource, salary.salarySource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, city, salarySource);
    }
}
