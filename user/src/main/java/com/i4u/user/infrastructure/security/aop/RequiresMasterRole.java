package com.i4u.user.infrastructure.security.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD) // 메서드에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('MASTER')") // MASTER 역할을 가진 사용자만 실행 가능
public @interface RequiresMasterRole {
}
