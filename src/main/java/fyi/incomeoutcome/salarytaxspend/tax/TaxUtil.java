package fyi.incomeoutcome.salarytaxspend.tax;

import fyi.incomeoutcome.salarytaxspend.salary.Salary;
import fyi.incomeoutcome.salarytaxspend.tax.Tax;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TaxUtil {

    @Value("${daysPerTaxRecord}")
    private int daysPerTaxRecord;

    public boolean dueNewTax(Tax tax, long currentTime){
        Salary relatedSalary = tax.getSalary();
        long compensationLastUpdated = relatedSalary.getUpdatedOn().getTime();
        long taxLastUpdated = tax.getUpdatedOn().getTime();
        long daysSinceLastTaxUpdate = TimeUnit.DAYS.convert(currentTime-taxLastUpdated, TimeUnit.MILLISECONDS);
        boolean taxNeverUpdated = taxLastUpdated == 0;
        boolean taxIsForOldCompensation = compensationLastUpdated > taxLastUpdated;
        boolean taxIsForLastYear = daysSinceLastTaxUpdate > daysPerTaxRecord;
        log.debug(String.format("For country %s, taxNeverUpdated %b, taxIsForOldCompensation %b ,taxIsForLastYear %b",
                relatedSalary.getCountry(), taxNeverUpdated, taxIsForOldCompensation, taxIsForLastYear));
        return (taxNeverUpdated || taxIsForOldCompensation || taxIsForLastYear );
    }
}
