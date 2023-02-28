package fyi.incomeoutcome.salarytaxspend.util;

import fyi.incomeoutcome.salarytaxspend.data.Salary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class SalaryUtil {

    @Value("${daysPerConversion}")
    private int daysPerCompensation;

    public boolean dueNewCompensation(Salary salary, long currentTime){
        if (salary.getUpdatedOn() == null){
            return true;
        }
        Date lastUpdated = new java.util.Date(salary.getUpdatedOn().getTime());
        long diffInMs = currentTime - lastUpdated.getTime();
        long daysSinceLastCompensation = TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
        return (daysSinceLastCompensation > daysPerCompensation);
    }
}
