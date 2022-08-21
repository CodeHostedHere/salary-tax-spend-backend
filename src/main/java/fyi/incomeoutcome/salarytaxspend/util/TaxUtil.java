package fyi.incomeoutcome.salarytaxspend.util;

import fyi.incomeoutcome.salarytaxspend.data.Salary;
import fyi.incomeoutcome.salarytaxspend.data.Tax;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TaxUtil {

    @Value("${daysPerTaxRecord}")
    private static int daysPerTaxRecord;

    public static boolean dueNewTax(Tax tax){
        Salary relatedSalary = tax.getSalary();
        long compensationLastUpdated = relatedSalary.getUpdatedOn().getTime();
        long taxLastUpdated = tax.getUpdatedOn().getTime();
        long today = System.currentTimeMillis();
        long daysSinceLastTaxUpdate = TimeUnit.DAYS.convert(today-taxLastUpdated, TimeUnit.MILLISECONDS);
        boolean taxNeverUpdated = taxLastUpdated == 0;
        boolean taxIsForOldCompenstion = compensationLastUpdated > taxLastUpdated;
        boolean taxIsForLastYear = daysSinceLastTaxUpdate > daysPerTaxRecord;
        log.debug(String.format("For country %s, taxNeverUpdated %b, taxIsForOldCompensation %b ,taxIsForLastYear %b",
                relatedSalary.getCountry(), taxNeverUpdated, taxIsForOldCompenstion, taxIsForLastYear));
        return (taxNeverUpdated || taxIsForOldCompenstion || taxIsForLastYear );
    }
}
