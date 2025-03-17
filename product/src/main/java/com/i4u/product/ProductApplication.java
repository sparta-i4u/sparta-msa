package com.i4u.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.i4u.common", "com.i4u.product"})
public class ProductApplication {

	//product branch test
	//product branch 생성
	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

}
