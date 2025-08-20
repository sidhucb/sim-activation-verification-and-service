package com.cardactivation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.cardactivation")
public class SimCardActivationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimCardActivationApplication.class, args);
	}

}
