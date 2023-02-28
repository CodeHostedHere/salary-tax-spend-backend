package fyi.incomeoutcome.salarytaxspend.service.datafetching;

import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import fyi.incomeoutcome.salarytaxspend.repository.TaxRepository;
import fyi.incomeoutcome.salarytaxspend.repository.source.TaxSourceRepository;
import fyi.incomeoutcome.salarytaxspend.util.TaxUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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