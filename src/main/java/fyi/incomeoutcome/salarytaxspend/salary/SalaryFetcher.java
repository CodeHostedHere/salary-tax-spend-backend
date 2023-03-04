package fyi.incomeoutcome.salarytaxspend.salary;

import fyi.incomeoutcome.salarytaxspend.service.scraper.GlassdoorScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SalaryFetcher {

    GlassdoorScraper glassdoorScraper;

    public SalaryFetcher(){};

    @Autowired
    public SalaryFetcher(GlassdoorScraper glassdoorScraper){
        this.glassdoorScraper = glassdoorScraper;
    }

    public List<Salary> fetchUpdatedSalaries(List<Salary> salariesToFetch){
        List<Salary> updatedSalaries = new ArrayList<>();
        for (Salary salaryToFetch: salariesToFetch){
            glassdoorScraper.setRoleCitySource(salaryToFetch.getRole(),
                    salaryToFetch.getCity(), salaryToFetch.getSalarySource());
            glassdoorScraper.executeScrape();
            log.info("Fetching Salary %s", salaryToFetch);
        }
        return updatedSalaries;
    }
}
