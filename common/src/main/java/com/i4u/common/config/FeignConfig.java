package com.i4u.common.config;

//import feign.codec.ErrorDecoder;
//import org.springframework.context.annotation.Bean;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.i4u.common.exception.GlobalFeignExceptionHandler;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GlobalFeignExceptionHandler();
    }
}
