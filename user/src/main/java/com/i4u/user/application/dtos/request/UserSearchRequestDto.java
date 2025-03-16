package com.i4u.user.application.dtos.request;

import com.i4u.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
@AllArgsConstructor // @NoArgsConstructor 제거 후 @AllArgsConstructor 사용
public class UserSearchRequestDto { // 검색 시 Role 필터 추가

    private final String keyword; // 불변성 유지 위해 final 추가
    private final UserRole role;
    private final Pageable pageable;

    // @Builder 사용 시 필드 초기화 문제 방지
    public static UserSearchRequestDto of(String keyword, UserRole role, Pageable pageable) {
        return UserSearchRequestDto.builder()
                .keyword(keyword)
                .role(role)
                .pageable(pageable)
                .build();
    }
}
