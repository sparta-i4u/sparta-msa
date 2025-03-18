package com.i4u.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
})
@ComponentScan(basePackages = {"com.i4u.common", "com.i4u.product"})
public class ProductApplication {

	//product branch test
	//product branch 생성
	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

}
