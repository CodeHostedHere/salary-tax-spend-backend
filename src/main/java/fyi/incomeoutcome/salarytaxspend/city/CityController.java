package fyi.incomeoutcome.salarytaxspend.city;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class CityController {

    private final CityRepository cityRepository;

    CityController(CityRepository cityRepository){
        this.cityRepository = cityRepository;
    }

    @GetMapping("/city/all")
    List<City> all() { return cityRepository.findAll(); }

}
