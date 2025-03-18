package com.i4u.user.application.dtos.request;

import com.i4u.user.domain.UserRole;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchRequestDto {

    @Size(min = 1, max = 50, message = "검색어는 1~50자 이내여야 합니다.")
    private final String keyword;

    private final UserRole role;
    private final Pageable pageable;

    // null-safe 변환 메서드 추가
    public static UserSearchRequestDto of(String keyword, UserRole role, Pageable pageable) {
        return UserSearchRequestDto.builder()
                .keyword(keyword != null ? keyword.trim() : "") // null 방지
                .role(role)
                .pageable(pageable != null ? pageable : PageRequest.of(0, 10)) // 기본 페이징 처리
                .build();
    }
}
