package fyi.incomeoutcome.salarytaxspend.repository;

import fyi.incomeoutcome.salarytaxspend.data.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {

    Optional<Currency> findByCurrencyCode(String currencyCode);
}
