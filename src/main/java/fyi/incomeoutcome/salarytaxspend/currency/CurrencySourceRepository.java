package fyi.incomeoutcome.salarytaxspend.currency;

import fyi.incomeoutcome.salarytaxspend.currency.CurrencySource;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface CurrencySourceRepository extends CrudRepository<CurrencySource, Long>{

    CurrencySource findById(long id);
}
