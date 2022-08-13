package fyi.incomeoutcome.salarytaxspend.repository;

import fyi.incomeoutcome.salarytaxspend.data.City;
import fyi.incomeoutcome.salarytaxspend.data.Spend;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpendRepository extends CrudRepository<Spend, Long> {
    Optional<Spend> findByCity(City city);

    @Query("SELECT s FROM Spend s WHERE s.convertedOn <?1 OR s.convertedOn is null")
    List<Spend> getRequiringConversion(java.sql.Date today);

    Spend findByCityId(long cityId);

}
