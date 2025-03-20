package com.i4u.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}

}
