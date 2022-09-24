package fyi.incomeoutcome.salarytaxspend;

import fyi.incomeoutcome.salarytaxspend.repository.SalaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("fyi.incomeoutcome.salarytaxspend.data")
@ComponentScan("fyi.incomeoutcome.salarytaxspend.service")
@ComponentScan("fyi.incomeoutcome.salarytaxspend.util")



@SpringBootApplication
public class SalaryTaxSpendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalaryTaxSpendApplication.class, args);
	}

}
