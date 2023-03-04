package fyi.incomeoutcome.salarytaxspend.tax;

import fyi.incomeoutcome.salarytaxspend.tax.TaxSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxSourceRepository extends CrudRepository<TaxSource, Long> {

    TaxSource findById(long id);
}
