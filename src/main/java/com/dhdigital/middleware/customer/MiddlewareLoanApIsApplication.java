package com.dhdigital.middleware.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class MiddlewareLoanApIsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiddlewareLoanApIsApplication.class, args);
	}

}
