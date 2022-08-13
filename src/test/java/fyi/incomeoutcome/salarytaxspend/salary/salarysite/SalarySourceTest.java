package fyi.incomeoutcome.salarytaxspend.salary.salarysite;

import fyi.incomeoutcome.salarytaxspend.data.source.SalarySource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SalarySourceTest {

    SalarySource testSource = new SalarySource("Glassdoor", "http://www.glassdoor.com","salElem", "monElem");

    @Test
    void getNameTest(){
        assertEquals(testSource.getName(), "Glassdoor");
    }

    @Test
    void getSearchUrlTest(){
        assertEquals(testSource.getSearchUrl(), "http://www.glassdoor.com");
    }

    @Test
    void getSalaryElementTest(){
        assertEquals(testSource.getSalaryElementClass(), "salElem");
    }
}
