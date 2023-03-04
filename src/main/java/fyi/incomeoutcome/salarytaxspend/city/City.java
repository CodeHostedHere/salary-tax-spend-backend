package fyi.incomeoutcome.salarytaxspend.city;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.spend.Spend;

import javax.persistence.*;
import java.util.Set;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String name;
    private String country;
    @OneToMany(mappedBy="city", cascade = CascadeType.ALL)
    private Set<Salary> salaries;
    @OneToOne(mappedBy = "city")
    private Spend spend;

    protected City(){}

    public City(String name, String country){
        this.name = name;
        this.country = country;

    }
    public long getId(){ return this.id; }

    public String getName(){ return this.name; }

    public String getCountry(){ return this.country; }

    public String toString(){
        return String.format("City[id=%d, name='%s', country = '%s']",
                id, name, country);
    }


}
