package com.i4u.common.config;

import com.i4u.common.utils.UserAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "userAuditorAware")
public class JpaAuditingConfig {

	@Bean(name = "jpaAuditorAware")
	public AuditorAware<UUID> userAuditorAware() {
		return new UserAuditorAware();
	}
}