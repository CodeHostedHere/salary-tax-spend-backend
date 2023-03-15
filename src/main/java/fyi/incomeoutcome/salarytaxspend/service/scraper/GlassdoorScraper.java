package fyi.incomeoutcome.salarytaxspend.service.scraper;

import fyi.incomeoutcome.salarytaxspend.role.RoleUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.commons.lang3.ArrayUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class GlassdoorScraper extends GoogleCustomSearchScraper {

    @Value("${countryWithJavaException}")
    protected String countryWithJavaException;
    @Value("${glassdoorMonthSpecification}")
    private String glassdoorMonthSpecification;
    @Value("${glassdoorMillionSpecification}")
    private char glassdoorMillionSpecification;
    @Value("${webPageFullUrlDelimiter}")
    private String webPageFullUrlDelimiter;

    public GlassdoorScraper(){}

    @Override
    public int fetchWebpageUrl(){
        String title = RoleUtil.getFullRoleTitle(role);
        title = title.replace(" ", webPageFullUrlDelimiter);
        String searchUrl = source.getSearchUrl();
        String cityName = city.getName();
        String countryName = city.getCountry();
        countryName = countryName.replace(" ", webPageFullUrlDelimiter);
        StringBuilder fullUrl = new StringBuilder(String.format("%s%s+%s,+%s", searchUrl, title, cityName,
                countryName));

        log.info(String.format("Fetching for %s %s %s %s %s", title, cityName, countryName, searchUrl, fullUrl));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl.toString()))
                .GET()
                .build();
        int statusCode = 0;
        try{
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            this.webPageUrl = findCorrectLink(response.body());
            log.info("Chose " + this.webPageUrl);
            return statusCode;
        } catch(Exception e) {
            log.error(e.toString());
            return statusCode;
        }
    }

    @Override
    protected String findCorrectLink(String searchResults){
        JSONArray jsonResultLink = new JSONObject(searchResults)
                .getJSONArray(googleResultItemKey);
        String roleSeniority = role.getSeniority();
        String[] relevantBadWords = ArrayUtils.removeElement(badWords, roleSeniority);

        // If there is no Junior Developer role for country, accept "junior java developer"
        if (this.city.getName().equals(countryWithJavaException)){
            relevantBadWords = ArrayUtils.removeElement(relevantBadWords, "Java");
        }
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

    @Override
    public String parsePageForSalary() {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.webPageUrl))
                .GET()
                .build();

        String salaryText = "";
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            Document doc = Jsoup.parse(response.body());
            Element salaryElement = doc.getElementsByClass(source.getSalaryElementClass()).first();
            Element monthOrAnnualElement = doc.getElementsByClass(source.getMonthOrAnnualElementClass()).first();
            assert monthOrAnnualElement != null;
            assert salaryElement != null;
            if (monthOrAnnualElement.text().contains(glassdoorMonthSpecification)){
                salaryText = salaryElement.text() + glassdoorMonthSpecification;
            } else {
                salaryText = salaryElement.text();
            }

            return salaryText;
        } catch (Exception e){
            log.error(e.toString());
        }
        return salaryText;
    }

    public void parseSalaryText(String salaryText){
        boolean perMonth = false;
        if (salaryText.contains(glassdoorMonthSpecification)){
            perMonth = true;
            salaryText = salaryText.substring(0, salaryText.length()-5);
        }
        // if millions specified with a character, convert to digits
        if (salaryText.charAt(salaryText.length()-1) == glassdoorMillionSpecification){
            salaryText = salaryText.substring(0, salaryText.length()-1);
            salaryText += "000000";
        }
        char[] salaryChars = salaryText.toCharArray();
        StringBuilder nonDigitCharacters = new StringBuilder();
        for (char c: salaryChars){
            if (!Character.isDigit(c)){
                salaryText = salaryText.replace(Character.toString(c),"");
                nonDigitCharacters.append(c);
            }
        }
        String currency = nonDigitCharacters.toString();
        currency = currency.replace(".","");
        currency = currency.replace(",","");
        log.info(this + " removed " + nonDigitCharacters + " from salary " + salaryText);
        this.compensation = Integer.parseInt(salaryText);
        if (perMonth) {
            // Monthly numbers need to be turned annual
            this.compensation *= 12;
        }
        this.currency  = currency;
    }

    @Override
    public String toString(){
        return String.format("GoogleCustomSearchScraper %s, %s, %s", RoleUtil.getFullRoleTitle(role),
                city.getName(), source.getName());
    }
}
