package com.i4u.common.utils;

import com.i4u.common.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserAuditorAware implements AuditorAware<UUID> {		// createdBy 및 lastModifiedBy 필드를 자동으로 채울 수 있도록 함

	private final String ANONYMOUS_USER = "anonymousUser";

	@Override
	public Optional<UUID> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals(ANONYMOUS_USER)) {
			return Optional.empty();
		}

		// UserDetails를 활용하여 UUID 반환 (UserDetailsImpl을 직접 참조하지 않음)
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return Optional.of(userDetails.getUserId());
		}

		return Optional.empty();
	}
}
