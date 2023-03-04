package fyi.incomeoutcome.salarytaxspend.salarysource;

import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySource;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface SalarySourceRepository extends CrudRepository<SalarySource, Long> {

    List<SalarySource> findAll();
}
