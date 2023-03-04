package fyi.incomeoutcome.salarytaxspend.salary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SalaryController {

    private final SalaryRepository salaryRepository;

    SalaryController(SalaryRepository salaryRepository){
        this.salaryRepository = salaryRepository;
    }

    @GetMapping("/salary/{cityId}")
    public List<Salary> getSalaryOfCity(@PathVariable long cityId){
        return salaryRepository.findByCityIdOrderByCityIdAsc(cityId);
    }

}
