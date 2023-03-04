package fyi.incomeoutcome.salarytaxspend.salary;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SalaryRefresher {
    private SalarySeedCreator salarySeedCreator;
    private SalaryDataVerifier salaryDataVerifier;
    private SalaryFetcher salaryFetcher;
    private SalaryRepository salaryRepository;

    public SalaryRefresher(){}

    @Autowired
    public SalaryRefresher(SalarySeedCreator salarySeedCreator, SalaryDataVerifier salaryDataVerifier,
                           SalaryFetcher salaryFetcher, SalaryRepository salaryRepository){
        this.salarySeedCreator = salarySeedCreator;
        this.salaryDataVerifier = salaryDataVerifier;
        this.salaryFetcher = salaryFetcher;
        this.salaryRepository = salaryRepository;
    }

    public void refreshAll(long currentTime){
        List<List<Object>> salaryRequiredComponents = salarySeedCreator.getRequiredSalaryComponents();
        List<Salary> outdatedSalaryList = salaryDataVerifier.getSalariesRequiringUpdate(salaryRequiredComponents, currentTime);
        List<Salary> updatedSalaryList = salaryFetcher.fetchUpdatedSalaries(outdatedSalaryList);
        salaryRepository.saveAll(updatedSalaryList);
    }
}
