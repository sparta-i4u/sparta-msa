//package com.i4u.user.testconfig;
//
//import com.i4u.user.application.UserService;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@TestConfiguration
//public class UserControllerIntegrationTestConfig {
//
//    @Bean(name = "mockUserService")
//    public UserService userService() {
//        return Mockito.mock(UserService.class);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return Mockito.mock(PasswordEncoder.class);
//    }
//}
