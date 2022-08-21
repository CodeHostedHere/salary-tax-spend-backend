package fyi.incomeoutcome.salarytaxspend.controller;

import fyi.incomeoutcome.salarytaxspend.data.Spend;
import fyi.incomeoutcome.salarytaxspend.repository.SpendRepository;

import fyi.incomeoutcome.salarytaxspend.util.SpendUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class SpendController {

    private SpendRepository spendRepository;

    SpendController(SpendRepository repository){
        this.spendRepository = repository;
    }

    @GetMapping("/spend/{cityId}")
    public ArrayList<Object[]> getSpendOfCity(@PathVariable long cityId){
        return SpendUtil.getSpendTableValues(spendRepository.findByCityId(cityId));
    }
}
