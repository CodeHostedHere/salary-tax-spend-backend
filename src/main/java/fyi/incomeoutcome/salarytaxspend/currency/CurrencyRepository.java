package fyi.incomeoutcome.salarytaxspend.currency;

import fyi.incomeoutcome.salarytaxspend.currency.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {

    Optional<Currency> findByCurrencyCode(String currencyCode);
}
