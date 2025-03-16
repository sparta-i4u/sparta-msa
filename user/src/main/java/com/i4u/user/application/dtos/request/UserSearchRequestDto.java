package com.i4u.user.application.dtos.request;

import com.i4u.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
@Builder
public class UserSearchRequestDto { // 검색 시 Role 필터 추가

    private String keyword;
    private UserRole role;
    private Pageable pageable;

    public UserSearchRequestDto(String keyword, UserRole role, Pageable pageable) {
        this.keyword = keyword;
        this.role = role;
        this.pageable = pageable;
    }
}
