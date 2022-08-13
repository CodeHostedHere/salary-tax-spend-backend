package fyi.incomeoutcome.salarytaxspend.repository.source;

import fyi.incomeoutcome.salarytaxspend.data.source.TaxSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxSourceRepository extends CrudRepository<TaxSource, Long> {

    TaxSource findById(long id);
}
