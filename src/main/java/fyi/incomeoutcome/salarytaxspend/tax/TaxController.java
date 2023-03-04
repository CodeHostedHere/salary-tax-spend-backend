package fyi.incomeoutcome.salarytaxspend.tax;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TaxController {

    private TaxRepository taxRepository;

    TaxController(TaxRepository repository){
        this.taxRepository = repository;
    }

    @GetMapping("/tax/{salaryId}")
    public Tax getTaxOfSalary(@PathVariable long salaryId){
        return taxRepository.findBySalaryId(salaryId);
    }
}
