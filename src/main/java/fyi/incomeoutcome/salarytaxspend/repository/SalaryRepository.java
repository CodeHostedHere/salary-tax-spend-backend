package fyi.incomeoutcome.salarytaxspend.repository;

import fyi.incomeoutcome.salarytaxspend.data.City;
import fyi.incomeoutcome.salarytaxspend.data.Role;
import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.data.source.SalarySource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends CrudRepository<Salary, Long> {


    Optional<Salary> findBySourceAndCityAndRole(SalarySource salarySource, City city, Role role);

    @Query("Select s FROM Salary s WHERE s.convertedOn < ?1 OR s.convertedOn is null")
    List<Salary> getRequiringConversion(java.sql.Date today);

    List<Salary> findByCityIdOrderByCityIdAsc(long cityid);

}
