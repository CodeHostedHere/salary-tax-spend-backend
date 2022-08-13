package fyi.incomeoutcome.salarytaxspend.salary.service;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoogleSearchScraperTest {

    /*private GoogleScraper scraper = new GoogleScraper("Senior Software Developer",
            "Dublin","https://www.googleapis.com/customsearch/v1?key=AIzaSyBUBZicU5aYfBW6DxDltB2t9YxTXBovGAM&cx=df69584a06671bc11&q=",
            "m-0 css-146zilq ebrouyy2");

    @Test
    void fetchWebpageUrlTest() { assertEquals(200, scraper.fetchWebpageUrl());}

    @Test
    void getWebPageUrlTest(){
        scraper.fetchWebpageUrl();
        assertTrue(scraper.getWebPageUrl().contains("https://www.glassdoor.com"));
    }

    @Test
    void parseUrlForSalaryTest(){
        scraper.fetchWebpageUrl();
        scraper.parseUrlForSalary();
        assertTrue(scraper.getSalary() > 0);
    }*/
}
