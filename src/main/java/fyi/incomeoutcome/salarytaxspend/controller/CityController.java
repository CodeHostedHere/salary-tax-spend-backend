package fyi.incomeoutcome.salarytaxspend.controller;

import fyi.incomeoutcome.salarytaxspend.data.City;
import fyi.incomeoutcome.salarytaxspend.repository.CityRepository;

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
