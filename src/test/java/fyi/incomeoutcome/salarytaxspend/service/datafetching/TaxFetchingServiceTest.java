package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import fyi.incomeoutcome.salarytaxspend.salary.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.tax.TaxFetchingService;
import fyi.incomeoutcome.salarytaxspend.tax.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.tax.TaxSourceRepository;
import fyi.incomeoutcome.salarytaxspend.tax.TaxUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class TaxFetchingServiceTest {

    @Mock
    TaxSourceRepository taxSourceRepository;
    @Mock
    TaxRepository taxRepository;
    @Mock
    SalaryRepository salaryRepository;
    @Mock
    TaxUtil taxUtil;

    @InjectMocks
    TaxFetchingService taxFetchingService;

    @Test
    void refreshAll() {
        Salary euroSalary = new Salary(30000, null, null, null, "EUR");
        Salary foreignSalary = new Salary(30000, null, null, null, "GBP");
        List<Salary> salaryList = Arrays.asList(euroSalary, foreignSalary);
        Tax euroTax = new Tax(euroSalary, 10000);
        Optional<Tax> presentTax = Optional.of(euroTax);
        Optional<Tax> emptyTax = Optional.empty();
    }
}