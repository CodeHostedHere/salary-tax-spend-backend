package fyi.incomeoutcome.salarytaxspend.salary;


import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.city.CityRepository;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import fyi.incomeoutcome.salarytaxspend.role.RoleRepository;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySource;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySourceRepository;
import fyi.incomeoutcome.salarytaxspend.service.datafetching.DataFetchingService;
import fyi.incomeoutcome.salarytaxspend.service.scraper.GlassdoorScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * The Salary object is the primary object that the program revolves around. This class
 * implements the website scraping, expiration checking and saving of this data.
 *
 * @author glacey
 */
@Component
@Slf4j
public class SalaryFetchingService implements DataFetchingService {

    private SalaryRepository salaryRepository;
    private CityRepository cityRepository;
    private SalarySourceRepository salarySourceRepository;
    private RoleRepository roleRepository;
    private GlassdoorScraper glassdoorScraper;
    private SalaryUtil salaryUtil;

    @Autowired
    public SalaryFetchingService(SalaryRepository salaryRepository, CityRepository cityRepository,
                                 SalarySourceRepository salarySourceRepository, RoleRepository roleRepository,
                                 GlassdoorScraper glassdoorScraper, SalaryUtil salaryUtil) {
        this.salaryRepository = salaryRepository;
        this.cityRepository = cityRepository;
        this.salarySourceRepository = salarySourceRepository;
        this.roleRepository = roleRepository;
        this.glassdoorScraper = glassdoorScraper;
        this.salaryUtil = salaryUtil;
    }


    /**
     *    Checks presence of every salary by querying every permutation of role, seniority and city.
     *    Uses the currentTime to compare against older records to see if salary requires updating.
     *
     *   @param currentTime time when function is called, used to compare against older records insertion time
     */
    @Override
    public void refreshAll(long currentTime){
        List<SalarySource> salarySourceList = new ArrayList<>();
        salarySourceRepository.findAll().forEach(salarySourceList::add);
        List<Role> roleList = new ArrayList<>();
        roleRepository.findAll().forEach(roleList::add);
        List<City> cityList = new ArrayList<>();
        cityRepository.findAll().forEach(cityList::add);
        log.debug(String.format("Role list %d", roleList.size()));
        log.debug(String.format("Site list %d", salarySourceList.size()));
        log.debug(String.format("City list %d", cityList.size()));

        // Todo: Test one large query vs individual query performance
        for (SalarySource salarySource : salarySourceList) {
            for (City city : cityList) {
                for (Role role : roleList) {
                    var foundSalaryOptional = salaryRepository.findBySourceAndCityAndRole(salarySource, city, role);
                    boolean salaryRequired = false;
                    if (foundSalaryOptional.isPresent()) {
                        Salary foundSalary = foundSalaryOptional.get();
                        if (salaryUtil.dueNewCompensation(foundSalary, currentTime)) {
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
