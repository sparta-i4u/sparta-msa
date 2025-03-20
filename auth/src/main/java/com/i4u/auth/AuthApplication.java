package com.i4u.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.i4u.common", "com.i4u.auth"}) // common을 통해서 빈을 받아올 수 있다.
public class AuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}
}
// base 패키지?
//참조할 수 있게 변행을 해줘야 한다.