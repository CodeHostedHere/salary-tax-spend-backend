package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.currency.Currency;
import fyi.incomeoutcome.salarytaxspend.currency.CurrencyFetchingService;
import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import fyi.incomeoutcome.salarytaxspend.currency.CurrencyRepository;
import fyi.incomeoutcome.salarytaxspend.salary.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.spend.SpendRepository;
import fyi.incomeoutcome.salarytaxspend.tax.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.currency.CurrencySourceRepository;
import fyi.incomeoutcome.salarytaxspend.spend.SpendUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CurrencyFetchingServiceTest {

    @Mock
    CurrencyRepository currencyRepository;
    @Mock
    CurrencySourceRepository currencySourceRepository;
    @Mock
    SalaryRepository salaryRepository;
    @Mock
    TaxRepository taxRepository;
    @Mock
    SpendRepository spendRepository;
    @Mock
    SpendUtil spendUtil;

    @InjectMocks
    CurrencyFetchingService currencyFetchingService;

    static Salary euroSalary;
    static Salary foreignSalary;
    static Tax euroTax;
    static Tax foreignTax;
    static Currency euroCurrency;
    static Currency foreignCurrency;
    static Optional<Currency> euroCurrencyOpt;
    static Optional<Currency> foreignCurrencyOpt;
    static long currentTime;
    static CurrencyFetchingService spyCFS;

    @BeforeAll
    static void init(){
        euroSalary = new Salary(30000, null, null, null, "EUR");
        foreignSalary = new Salary(30000, null, null, null, "GBP");
        foreignCurrency = new Currency("GBP", 0.88);
        foreignCurrencyOpt = Optional.of(foreignCurrency);
        euroCurrency = new Currency("EUR", 1.00);
        euroCurrencyOpt = Optional.of(euroCurrency);
        euroTax = new Tax(euroSalary, 10000);
        foreignTax = new Tax(foreignSalary, 10000);
        currentTime = System.currentTimeMillis();
    }

    @Test
    void refreshAllVerifyMethodsTest() {
        CurrencyFetchingService spyCFS = spy(currencyFetchingService);
        spyCFS.refreshAll(currentTime);
        verify(spyCFS, Mockito.times(1)).refreshAllSalaries(currentTime);
        verify(spyCFS, Mockito.times(1)).refreshAllTax(currentTime);
        verify(spyCFS, Mockito.times(1)).refreshAllSpend(currentTime);
    }

    @Test
    void refreshAllSalariesTest() {
        Salary euroSalarySpy = spy(euroSalary);
        Salary foreignSalarySpy = spy(foreignSalary);
        CurrencyFetchingService spyCFS = spy(currencyFetchingService);
        List<Salary> salaryList = Arrays.asList(euroSalarySpy, foreignSalarySpy);
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);

        when(salaryRepository.getRequiringConversion(currentDateTime)).thenReturn(salaryList);
        when(currencyRepository.findByCurrencyCode("EUR")).thenReturn(Optional.of(euroCurrency));
        when(currencyRepository.findByCurrencyCode("GBP")).thenReturn(Optional.of(foreignCurrency));
        doReturn(30000.0).when(spyCFS).convertSalaryToEuro(any());
        doNothing().when(euroSalarySpy).setCompensationConverted(30000.0);
        doNothing().when(foreignSalarySpy).setCompensationConverted(30000.0);
        doReturn("Euro Salary").when(euroSalarySpy).toString();
        doReturn("Foreign Salary").when(foreignSalarySpy).toString();
        when(salaryRepository.save(any())).thenReturn(euroSalary);

        spyCFS.refreshAllSalaries(currentTime);
        verify(spyCFS, Mockito.times(2)).convertSalaryToEuro(any());
    }

    @Test
    void refreshAllTax() {
        CurrencyFetchingService spyCFS = spy(currencyFetchingService);
        java.sql.Date currentDateTime = new java.sql.Date(currentTime);
        Tax euroTaxSpy = spy(euroTax);
        Tax foreignTaxSpy = spy(foreignTax);
        List<Tax> taxList = Arrays.asList(euroTaxSpy, foreignTaxSpy);

        when(taxRepository.getRequiringConversion(currentDateTime)).thenReturn(taxList);
        when(currencyRepository.findByCurrencyCode("EUR")).thenReturn(Optional.of(euroCurrency));
        when(currencyRepository.findByCurrencyCode("GBP")).thenReturn(Optional.of(foreignCurrency));
        doReturn(10000.0).when(spyCFS).convertTaxToEuro(any());
        doNothing().when(euroTaxSpy).setTaxPayableConverted(10000.0);
        doNothing().when(foreignTaxSpy).setTaxPayableConverted(10000.0);
        when(taxRepository.save(any())).thenReturn(euroTax);

        spyCFS.refreshAllTax(currentTime);
        verify(spyCFS, Mockito.times(2)).convertTaxToEuro(any());
    }

    @Test
    void convertSalaryToEuroFromEuro() {
        when(currencyRepository.findByCurrencyCode("EUR")).thenReturn(euroCurrencyOpt);
        assertEquals(30000, currencyFetchingService.convertSalaryToEuro(euroSalary));
    }

    @Test
    void convertSalaryToEuroFromForeignCurrency() {
        when(currencyRepository.findByCurrencyCode("GBP")).thenReturn(foreignCurrencyOpt);
        assertEquals(34090.91, currencyFetchingService.convertSalaryToEuro(foreignSalary));
    }

    @Test
    void convertTaxToEuroFromEuroTest() {
        when(currencyRepository.findByCurrencyCode("EUR")).thenReturn(euroCurrencyOpt);
        assertEquals(10000, currencyFetchingService.convertTaxToEuro(euroTax));
    }

    @Test
    void convertTaxToEuroFromForeignCurrencyTest() {
        when(currencyRepository.findByCurrencyCode("GBP")).thenReturn(foreignCurrencyOpt);
        assertEquals(11363.64, currencyFetchingService.convertTaxToEuro(foreignTax));
    }

    @Test
    void convertFigureToEuroFromEuroTest() {
        when(currencyRepository.findByCurrencyCode("EUR")).thenReturn(euroCurrencyOpt);
        assertEquals(100.0, currencyFetchingService.convertFigureToEuro(100, "EUR"));
    }

    @Test
    void convertFigureToEuroFromForeignCurrencyTest() {
        when(currencyRepository.findByCurrencyCode("GBP")).thenReturn(foreignCurrencyOpt);
        assertEquals(100.0, currencyFetchingService.convertFigureToEuro(88, "GBP"));
    }

}