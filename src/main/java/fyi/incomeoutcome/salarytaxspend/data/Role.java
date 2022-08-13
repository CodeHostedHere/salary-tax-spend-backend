package fyi.incomeoutcome.salarytaxspend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONPropertyIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String seniority;
    private String roleName;
    @OneToMany(mappedBy="role", cascade = CascadeType.ALL)
    private Set<Salary> salaries;

    protected Role(){}

    public Role(String seniority,String roleName){
        this.seniority = seniority;
        this.roleName = roleName;
    }

    public long getId(){ return this.id; }

    public String getSeniority(){
        return this.seniority;
    }

    public String getRole(){ return this.roleName; }

    @JsonIgnore
    public String getFullRoleTitle(){
        if (this.seniority.equals("")){
            return String.format("%s %s", this.seniority, this.roleName);
        } else{
            return roleName;
        }
    }

    public String toString(){
        return String.format("id=%d, %s %s", id, seniority, roleName);
    }

}
