package fyi.incomeoutcome.salarytaxspend.salary;

import com.google.common.collect.Lists;
import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.city.CityRepository;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import fyi.incomeoutcome.salarytaxspend.role.RoleRepository;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySource;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.cartesianProduct;

@Slf4j
@Service
public class SalarySeedCreator {
    private SalarySourceRepository salarySourceRepository;
    private RoleRepository roleRepository;
    private CityRepository cityRepository;

    public SalarySeedCreator(){}

    @Autowired
    public SalarySeedCreator(SalarySourceRepository salarySourceRepository, RoleRepository roleRepository,
                             CityRepository cityRepository){
        this.salarySourceRepository = salarySourceRepository;
        this.roleRepository = roleRepository;
        this.cityRepository = cityRepository;
    }

    public List<List<Object>> getRequiredSalaryComponents(){
        List<SalarySource> salarySourceList = new ArrayList<>();
        salarySourceRepository.findAll().forEach(salarySourceList::add);
        List<Role> roleList = new ArrayList<>();
        roleRepository.findAll().forEach(roleList::add);
        List<City> cityList = new ArrayList<>();
        cityRepository.findAll().forEach(cityList::add);
        log.debug(String.format("Role list %d", roleList.size()));
        log.debug(String.format("Site list %d", salarySourceList.size()));
        log.debug(String.format("City list %d", cityList.size()));
        List<List<Object>> requiredSalaryComponents = Lists.cartesianProduct(salarySourceList, cityList, roleList);
        return requiredSalaryComponents;
    }
}
