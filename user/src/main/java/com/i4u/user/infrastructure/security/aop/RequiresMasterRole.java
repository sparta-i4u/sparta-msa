package com.i4u.user.infrastructure.security.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD) // 메서드에 적용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresMasterRole {
}
