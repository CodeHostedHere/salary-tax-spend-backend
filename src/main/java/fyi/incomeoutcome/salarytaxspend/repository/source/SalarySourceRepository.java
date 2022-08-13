package fyi.incomeoutcome.salarytaxspend.repository.source;

import fyi.incomeoutcome.salarytaxspend.data.source.SalarySource;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface SalarySourceRepository extends CrudRepository<SalarySource, Long> {

}
