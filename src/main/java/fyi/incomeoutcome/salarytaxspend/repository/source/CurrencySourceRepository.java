package fyi.incomeoutcome.salarytaxspend.repository.source;

import fyi.incomeoutcome.salarytaxspend.data.source.CurrencySource;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface CurrencySourceRepository extends CrudRepository<CurrencySource, Long>{

    CurrencySource findById(long id);
}
