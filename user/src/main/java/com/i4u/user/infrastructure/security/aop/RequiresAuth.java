package com.i4u.user.infrastructure.security.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD) // 메서드에 적용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuth {
}
//1⃣ 로그인이 필요한 API 메서드에 적용
//2⃣ JWT 인증을 기반으로 SecurityContext에서 사용자 정보 검증
//3⃣ 로그인되지 않은 경우 예외 발생 (PERMISSION_DENIED)