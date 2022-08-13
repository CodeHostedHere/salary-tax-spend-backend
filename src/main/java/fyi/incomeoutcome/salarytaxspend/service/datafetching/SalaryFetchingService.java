package fyi.incomeoutcome.salarytaxspend.service.datafetching;


import fyi.incomeoutcome.salarytaxspend.data.City;
import fyi.incomeoutcome.salarytaxspend.data.source.SalarySource;
import fyi.incomeoutcome.salarytaxspend.repository.CityRepository;
import fyi.incomeoutcome.salarytaxspend.data.Role;
import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.repository.RoleRepository;
import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.SalarySourceRepository;
import fyi.incomeoutcome.salarytaxspend.service.scraper.GlassdoorScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SalaryFetchingService implements DataFetchingService {
    @Autowired
    private SalaryRepository salaryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private SalarySourceRepository siteRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GlassdoorScraper glassdoorScraper;

    @Override
    public void refreshAll(){
        List<SalarySource> salarySourceList = new ArrayList<>();
        siteRepository.findAll().forEach(salarySourceList::add);
        List<Role> roleList = new ArrayList<>();
        roleRepository.findAll().forEach(roleList::add);
        List<City> cityList = new ArrayList<>();
        cityRepository.findAll().forEach(cityList::add);
        log.debug(String.format("Role list %d", roleList.size()));
        log.debug(String.format("Site list %d", salarySourceList.size()));
        log.debug(String.format("City list %d", cityList.size()));

        for (SalarySource salarySource : salarySourceList) {
            for (City city : cityList) {
                for (Role role : roleList) {
                    var foundSalaryOptional = salaryRepository.findBySourceAndCityAndRole(salarySource, city, role);
                    boolean salaryRequired = false;
                    if (foundSalaryOptional.isPresent()) {
                        Salary foundSalary = foundSalaryOptional.get();
                        if (foundSalary.dueNewCompensation()) {
                            log.debug(String.format("Salary compensation out of date for: %s", foundSalary));
                            salaryRequired = true;
                        }
                    } else {
                        log.debug(String.format("Salary not found for %s %s %s", salarySource, city, role));
                        salaryRequired = true;
                    }
                    if (salaryRequired){
                        fetchAndSaveSalary(salarySource, city, role);
                    }
                }
            }
        }
    }

    public void fetchAndSaveSalary(SalarySource salarySource, City city, Role role){
        glassdoorScraper.setRoleCitySource(role, city, salarySource);
        Salary missingSalary = glassdoorScraper.executeScrape();
        log.info(String.format("Saving Salary %s", missingSalary));
        salaryRepository.save(missingSalary);
    }
}
