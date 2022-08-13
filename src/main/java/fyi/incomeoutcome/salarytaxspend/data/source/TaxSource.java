package fyi.incomeoutcome.salarytaxspend.data.source;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TaxSource {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;
    private String urlBase;
    private String urlEnd;

    public String getUrlBase(){ return this.urlBase; }

    public String getUrlEnd(){ return this.urlEnd; }
}
