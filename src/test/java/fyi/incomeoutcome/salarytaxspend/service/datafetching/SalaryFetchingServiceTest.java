package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.salary.*;
import fyi.incomeoutcome.salarytaxspend.city.CityRepository;
import fyi.incomeoutcome.salarytaxspend.role.RoleRepository;
import fyi.incomeoutcome.salarytaxspend.salary.SalarySourceRepository;
import fyi.incomeoutcome.salarytaxspend.service.scraper.GlassdoorScraper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
class SalaryFetchingServiceTest {

    @Mock
    SalaryRepository salaryRepository;
    @Mock
    CityRepository cityRepository;
    @Mock
    SalarySourceRepository salarySourceRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    GlassdoorScraper glassdoorScraper;


   // @InjectMocks
    //SalaryFetchingService salaryFetchingService;

    @BeforeAll
    static void init() {

    }

    @Test
    void refreshAllTest() {
        /*SalarySource salarySource = new SalarySource("test", "searchUrl", "salaryElement", "monthOrAnnualElement");
        List<SalarySource> salarySourceList = Arrays.asList(salarySource);
        Role roleOne = new Role("Senior", "Software Engineer");
        Role roleTwo = new Role("Junior", "Software Engineer");
        List<Role> roleList = Arrays.asList(roleOne, roleTwo);
        City cityOne = new City("Dublin", "Ireland");
        City cityTwo = new City("Berlin", "Germany");
        City cityThree = new City("Zurich", "Switzerland");
        List<City> cityList = Arrays.asList(cityOne, cityTwo, cityThree);
        Optional<Salary> emptySalary = Optional.empty();
        Salary salaryOne = new Salary(30000, roleOne, cityOne, salarySource, "EUR");
        Optional<Salary> foundSalaryUnconverted = Optional.of(salaryOne);
        long currentTime = System.currentTimeMillis();

        when(salarySourceRepository.findAll()).thenReturn(salarySourceList);
        when(salaryRepository.findBySalarySourceAndCityAndRole(salarySource,  cityOne, roleOne))
                .thenReturn(foundSalaryUnconverted)
                .thenReturn(emptySalary);
        when(roleRepository.findAll()).thenReturn(roleList);
        when(cityRepository.findAll()).thenReturn(cityList);
        when(salaryUtil.dueNewCompensation(salaryOne, currentTime)).thenReturn(true);

        /*SalaryFetchingService spySFS = spy(salaryFetchingService);
        currentTime = System.currentTimeMillis();
        spySFS.refreshAll(currentTime);
        verify(spySFS, Mockito.times(1)).refreshAll(currentTime);
        verify(salaryUtil, Mockito.times(1)).dueNewCompensation(salaryOne, currentTime);*/
    }
}