package fyi.incomeoutcome.salarytaxspend.role;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;

import javax.persistence.*;
import java.util.Objects;
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

    public String getRoleName(){ return this.roleName; }

    public String toString(){
        return String.format("id=%d, %s %s", id, seniority, roleName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return seniority.equals(role.seniority) && roleName.equals(role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seniority, roleName);
    }
}
