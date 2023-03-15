package fyi.incomeoutcome.salarytaxspend.salary;

import com.google.common.collect.Lists;
import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.city.CityRepository;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import fyi.incomeoutcome.salarytaxspend.role.RoleRepository;
import fyi.incomeoutcome.salarytaxspend.scraper.GlassdoorScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.cartesianProduct;

@Slf4j
@Service
public class SalaryDataLoader {
    private SalarySourceRepository salarySourceRepository;
    private RoleRepository roleRepository;
    private CityRepository cityRepository;
    private SalaryRepository salaryRepository;
    private GlassdoorScraper glassdoorScraper;

    @Value("${daysPerCompensation}")
    private String daysPerCompensation;

    @Autowired
    public SalaryDataLoader(SalarySourceRepository salarySourceRepository, RoleRepository roleRepository,
                            CityRepository cityRepository, SalaryRepository salaryRepository, GlassdoorScraper glassdoorScraper){
        this.salarySourceRepository = salarySourceRepository;
        this.roleRepository = roleRepository;
        this.cityRepository = cityRepository;
        this.salaryRepository = salaryRepository;
        this.glassdoorScraper = glassdoorScraper;
    }

    public void loadData(long currentTime){
        List<Salary> requiredSalaries = getRequiredSalaries();
        List<Salary> rem = getSalariesNotRequiringUpdate(currentTime);
        log.info(String.format("Got required salaries %d", requiredSalaries.size()));
        log.info(String.format("Got no update salaries %d", rem.size()));

        requiredSalaries.removeAll(rem);
        log.info(String.format("Left with required salaries %d", requiredSalaries.size()));

        for (Salary salaryToFetch: requiredSalaries){
            log.info("Fetching Salary %s", salaryToFetch);
            glassdoorScraper.setRoleCitySource(salaryToFetch.getRole(),
                    salaryToFetch.getCity(), salaryToFetch.getSalarySource());
            glassdoorScraper.executeScrape();
        }
    }

    public List<Salary> getRequiredSalaries(){
        List<SalarySource> salarySourceList = salarySourceRepository.findAll();
        List<Role> roleList = roleRepository.findAll();
        List<City> cityList = cityRepository.findAll();
        List<Salary> requiredSalaries = new ArrayList<>();
        for (List<Object> salaryComponents : Lists.cartesianProduct(salarySourceList, cityList, roleList)) {
            requiredSalaries.add(new Salary((Role) salaryComponents.get(2),(City) salaryComponents.get(1), (SalarySource) salaryComponents.get(0)));
        }
        return requiredSalaries;
    }

    public List<Salary> getSalariesNotRequiringUpdate(long currentTime){
        long dayCutOff = currentTime - TimeUnit.DAYS.toMillis((Long.parseLong(daysPerCompensation)));
        List<Salary> notRequiringUpdateSalaries = salaryRepository.findByUpdatedOnGreaterThan(new java.sql.Date(dayCutOff));
        return notRequiringUpdateSalaries;
    }
}
