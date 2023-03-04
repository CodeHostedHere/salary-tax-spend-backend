package fyi.incomeoutcome.salarytaxspend.salary;

import fyi.incomeoutcome.salarytaxspend.salary.SalarySource;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface SalarySourceRepository extends CrudRepository<SalarySource, Long> {

    List<SalarySource> findAll();
}
