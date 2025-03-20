package com.i4u.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
})

@EnableFeignClients
@ComponentScan(basePackages = {"com.i4u.common", "com.i4u.company"})
public class CompanyApplication {

	//Company branch 생성
	public static void main(String[] args) {
		SpringApplication.run(CompanyApplication.class, args);
	}

}