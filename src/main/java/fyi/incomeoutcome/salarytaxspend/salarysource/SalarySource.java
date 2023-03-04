package fyi.incomeoutcome.salarytaxspend.salarysource;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SalarySource {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String name;
    private String searchUrl;
    private String salaryElementClass;
    private String monthOrAnnualElementClass;

    protected SalarySource(){}

    @Autowired
    public SalarySource(String name, String searchUrl, String salaryElementClass, String monthOrAnnualElementClass){
        this.name = name;
        this.searchUrl = searchUrl;
        this.salaryElementClass = salaryElementClass;
        this.monthOrAnnualElementClass = monthOrAnnualElementClass;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return String.format("%s %s %s", name, searchUrl, salaryElementClass);
    }

    public String getSearchUrl(){
        return searchUrl;
    }

    public String getSalaryElementClass(){
        return salaryElementClass;
    }

    public String getMonthOrAnnualElementClass() { return monthOrAnnualElementClass;}
}
