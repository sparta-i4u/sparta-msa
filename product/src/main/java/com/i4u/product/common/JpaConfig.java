package com.i4u.product.common;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EntityScan(basePackages = "com.i4u.product.domain")
@Configuration
public class JpaConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em); // EntityManager를 주입하여 JPAQueryFactory 초기화
    }
}
