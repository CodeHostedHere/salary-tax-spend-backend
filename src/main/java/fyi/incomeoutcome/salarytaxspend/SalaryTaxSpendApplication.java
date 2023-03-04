package fyi.incomeoutcome.salarytaxspend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;

@Configuration




@SpringBootApplication
@ComponentScan("fyi.incomeoutcome.salarytaxspend")
@EntityScan("fyi.incomeoutcome.salarytaxspend")
public class SalaryTaxSpendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalaryTaxSpendApplication.class, args);
	}

}
