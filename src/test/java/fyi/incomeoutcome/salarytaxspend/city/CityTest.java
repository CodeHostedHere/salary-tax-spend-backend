package fyi.incomeoutcome.salarytaxspend.city;

import fyi.incomeoutcome.salarytaxspend.data.City;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CityTest {
    City testCity = new City("Dublin", "Ireland");

    @Test
    void getNameTest(){
        assertTrue(testCity.getName() == "Dublin");
    }

    @Test
    void getCountryTest(){
        assertTrue(testCity.getCountry() == "Ireland");
    }

    @Test
    void toStringTest(){
        assertTrue(testCity.toString().contains("Dublin"));
        assertTrue(testCity.toString().contains("Ireland"));
    }

}
