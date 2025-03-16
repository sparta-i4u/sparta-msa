package com.i4u.user.application.dtos.response;

import com.i4u.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserListResponseDto {

    private final List<UserDetailResponseDto> users;
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;

    public UserListResponseDto(Page<User> page) {
        this.users = page.getContent().stream()
                .map(UserDetailResponseDto::from)
                .collect(Collectors.toList());
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
    }
}

//전체 데이테 갯수, 전체 페이지 개수, 현재 페이지 번호 이런 식으로 ?? 리스트로 담고 페이징