package com.kcbgroup.kcbbnplloanreceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KcbBnplLoanreceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(KcbBnplLoanreceiverApplication.class, args);
	}

}
