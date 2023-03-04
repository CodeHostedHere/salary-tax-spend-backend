package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.city.City;
import fyi.incomeoutcome.salarytaxspend.spend.Spend;
import fyi.incomeoutcome.salarytaxspend.spend.SpendFetchingService;
import fyi.incomeoutcome.salarytaxspend.spendsource.SpendSource;
import fyi.incomeoutcome.salarytaxspend.city.CityRepository;
import fyi.incomeoutcome.salarytaxspend.spend.SpendRepository;
import fyi.incomeoutcome.salarytaxspend.spendsource.SpendSourceRepository;
import fyi.incomeoutcome.salarytaxspend.spend.SpendUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class SpendFetchingServiceTest {

    @Mock
    CityRepository cityRepository;
    @Mock
    SpendRepository spendRepository;
    @Mock
    SpendSourceRepository spendSourceRepository;
    @Mock
    SpendUtil spendUtil;
    @Mock
    SpendSource spendSource;

    @InjectMocks
    SpendFetchingService spendFetchingService;


    @Test
    void refreshAllTest() {
        City cityOne = new City("Dublin", "Ireland");
        City cityTwo = new City("Berlin", "Germany");
        City cityThree = new City("Zurich", "Switzerland");
        List<City> cityList = Arrays.asList(cityOne, cityTwo, cityThree);
        long currentTime = System.currentTimeMillis();
        java.sql.Date today = new Date(currentTime);
        Spend spendOne = new Spend(cityOne, "EUR", today, today);
        Optional<Spend> presentSpend = Optional.of(spendOne);
        Optional<Spend> emptySpend = Optional.empty();


        when(cityRepository.findAll()).thenReturn(cityList);
        when(spendRepository.findByCity(any())).thenReturn(presentSpend)
                .thenReturn(emptySpend);

        SpendFetchingService spySFS = spy(spendFetchingService);
        doNothing().when(spySFS).fetchAndSaveSpend(any());
        spySFS.refreshAll(currentTime);
        verify(spySFS, Mockito.times(1)).refreshAll(currentTime);
    }
}