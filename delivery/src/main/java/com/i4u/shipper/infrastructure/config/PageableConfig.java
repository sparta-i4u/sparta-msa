package com.i4u.shipper.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

	/**
	 * Pageable 기본 설정을 담은 메서드
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		// 기본 설정값은 0페이지, 10개, 생성일 내림차순
		resolver.setFallbackPageable(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
		resolvers.add(resolver);
	}
}
