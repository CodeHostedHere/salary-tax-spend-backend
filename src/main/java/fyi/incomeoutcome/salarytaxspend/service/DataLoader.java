package fyi.incomeoutcome.salarytaxspend.service;

import fyi.incomeoutcome.salarytaxspend.service.datafetching.CurrencyFetchingService;
import fyi.incomeoutcome.salarytaxspend.service.datafetching.SalaryFetchingService;
import fyi.incomeoutcome.salarytaxspend.service.datafetching.SpendFetchingService;
import fyi.incomeoutcome.salarytaxspend.service.datafetching.TaxFetchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Slf4j
public class DataLoader {
    @Autowired
    private SpendFetchingService spendFetchingService;
    @Autowired
    private CurrencyFetchingService currencyFetchingService;
    @Autowired
    private TaxFetchingService taxFetchingService;
    @Autowired
    private SalaryFetchingService salaryFetchingService;

    @PostConstruct
    public void initialDataLoad() {
        long currentTime = System.currentTimeMillis();
        salaryFetchingService.refreshAll(currentTime);
        taxFetchingService.refreshAll(currentTime);
        spendFetchingService.refreshAll(currentTime);
        currencyFetchingService.refreshAll(currentTime);
    }
}
