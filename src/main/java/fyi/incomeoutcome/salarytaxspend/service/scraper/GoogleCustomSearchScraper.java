package fyi.incomeoutcome.salarytaxspend.service.scraper;

import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.role.Role;
import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.salarysource.SalarySource;
import fyi.incomeoutcome.salarytaxspend.salary.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.role.RoleUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public abstract class GoogleCustomSearchScraper {
    protected Role role;
    protected City city;
    protected SalarySource source;

    protected String webPageUrl;
    protected int compensation;
    protected String currency;

    @Value("${badWords}")
    protected String[] badWords;
    @Value("${googleResultItemKey}")
    protected String googleResultItemKey;
    @Value("${googleResultTitleKey}")
    protected String googleResultTitleKey;
    @Value("${googleResultUrlKey}")
    protected String googleResultUrlKey;

    @Autowired
    SalaryRepository salaryRepo;

    public abstract int fetchWebpageUrl();
    public abstract String parsePageForSalary();
    public abstract void parseSalaryText(String salaryText);

    public void setRoleCitySource(Role role, City city, SalarySource source){
        this.role = role;
        this.city = city;
        this.source = source;
    }

    public Salary executeScrape(){
        log.info("Executing scrape");
        int statusCodeFetchUrl = fetchWebpageUrl();
        log.info(String.format("fetchWebPageUrl for %s : %d", this.source.getSearchUrl(), statusCodeFetchUrl));
        String salaryText = parsePageForSalary();
        log.info(String.format("parseUrl for salary %s : %s", this.city, salaryText));
        parseSalaryText(salaryText);
        return saveSalary();
    }

    protected String findCorrectLink(String searchResults){
        JSONArray jsonResultLink = new JSONObject(searchResults)
                .getJSONArray(googleResultItemKey);
        String roleSeniority = role.getSeniority();
        String[] relevantBadWords = ArrayUtils.removeElement(badWords, roleSeniority);

        int correctSearchResultIndex = 0;
        for (int i=0; i < 10; i++){
            boolean noBadWords = true;
            String pageTitle = jsonResultLink.getJSONObject(i).getString(googleResultTitleKey);
            if (!pageTitle.contains(roleSeniority) && !pageTitle.contains(roleSeniority.toLowerCase())){
                continue;
            }
            log.info("Page Title: " + pageTitle);
            for (String badWord: relevantBadWords){
                log.info("Checking bad word " + badWord);
                if (pageTitle.contains(badWord) | pageTitle.contains(badWord.toLowerCase())){
                    noBadWords = false;
                    log.info("Found Bad Word " + badWord);
                    break;
                }
            }
            if (noBadWords){
                correctSearchResultIndex = i;
                log.info("No Bad Word Found for result number " + i);
                break;
            }
        }
        return jsonResultLink.getJSONObject(correctSearchResultIndex)
                .getString(googleResultUrlKey);
    }
    
    protected Salary saveSalary(){
        Salary scrapedSalary = new Salary(this.compensation, this.role, this.city, this.source, this.currency);
        log.info("saving " + scrapedSalary);
        salaryRepo.save(scrapedSalary);
        return scrapedSalary;
    }

    public int getSalary() {
        if (this.compensation == 0){
            parsePageForSalary();
            return this.compensation;
        }
        return this.compensation;
    }

    public String toString(){
        return String.format("GoogleCustomSearchScraper %s, %s, %s", RoleUtil.getFullRoleTitle(role),
                city.getName(), source.getName());
    }

}
