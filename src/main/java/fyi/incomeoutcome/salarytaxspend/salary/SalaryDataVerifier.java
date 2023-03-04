package fyi.incomeoutcome.salarytaxspend.salary;

import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SalaryDataVerifier {

    private SalaryRepository salaryRepository;
    private SalaryUtil salaryUtil;

    public SalaryDataVerifier(){};

    @Autowired
    public SalaryDataVerifier(SalaryRepository salaryRepository, SalaryUtil salaryUtil){
        this.salaryRepository = salaryRepository;
        this.salaryUtil = salaryUtil;
    }

    public List<Salary> getSalariesRequiringUpdate(List<List<Object>> requiredSalaryComponents, long currentTime){
        List<Salary> savedSalaries = salaryRepository.findAll();
        List<Salary> salariesRequiringUpdate = new ArrayList();
        for (List<Object> requiredSalaryComponent : requiredSalaryComponents){
            SalarySource salarySourceRequired = (SalarySource) requiredSalaryComponent.get(0);
            Role roleRequired = (Role) requiredSalaryComponent.get(1);
            City cityRequired = (City) requiredSalaryComponent.get(2);
            Salary requiredSalary = savedSalaries.stream()
                    .filter(savedSalary -> cityRequired.equals(savedSalary.getCity()))
                    .filter(savedSalary -> roleRequired.equals(savedSalary.getRole()))
                    .filter(savedSalary -> salarySourceRequired.equals(savedSalary.getSalarySource()))
                    .findAny()
                    .orElse(null);
            if (requiredSalary == null || salaryUtil.dueNewCompensation(requiredSalary, currentTime)) {
                salariesRequiringUpdate.add(requiredSalary);
                savedSalaries.remove(requiredSalary);
            }
        }
        return salariesRequiringUpdate;
    }
}


