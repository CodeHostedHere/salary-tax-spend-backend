package fyi.incomeoutcome.salarytaxspend.tax;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRepository extends CrudRepository<Tax, Long> {

    Optional<Tax> findBySalary(Salary salary);

    @Query("SELECT t FROM Tax t WHERE t.convertedOn <?1 OR t.convertedOn is null")
    List<Tax> getRequiringConversion(java.sql.Date today);

    Tax findBySalaryId(long salaryid);

}
