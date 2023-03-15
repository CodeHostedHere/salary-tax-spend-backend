package fyi.incomeoutcome.salarytaxspend;

import fyi.incomeoutcome.salarytaxspend.currency.CurrencyFetchingService;
import fyi.incomeoutcome.salarytaxspend.salary.*;
import fyi.incomeoutcome.salarytaxspend.spend.SpendFetchingService;
import fyi.incomeoutcome.salarytaxspend.tax.TaxFetchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


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
    private SalaryDataLoader salaryDataLoader;


    @PostConstruct
    public void dataLoad() {
        long currentTime = System.currentTimeMillis();
        // Initially was going to separate out fetching services and create
        // a dataloading service but have decided to move on from this project
        salaryDataLoader.loadData(currentTime);
        taxFetchingService.refreshAll(currentTime);
        spendFetchingService.refreshAll(currentTime);
        currencyFetchingService.refreshAll(currentTime);
    }
}
