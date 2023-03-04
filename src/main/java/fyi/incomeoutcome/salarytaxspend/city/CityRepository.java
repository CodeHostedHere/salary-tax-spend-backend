package fyi.incomeoutcome.salarytaxspend.city;

import fyi.incomeoutcome.salarytaxspend.city.City;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {

    List<City> findAll();

}
