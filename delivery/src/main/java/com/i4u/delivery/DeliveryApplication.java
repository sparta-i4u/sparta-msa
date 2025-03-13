package com.i4u.delivery;

import com.i4u.common.TestBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeliveryApplication {

	TestBean testBean;

	public static void main(String[] args) {
		SpringApplication.run(DeliveryApplication.class, args);
	}

}
