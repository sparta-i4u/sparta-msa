package com.i4u.user.application.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.i4u.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserListResponseDto {

    private final List<UserDetailResponseDto> users;
    private final long totalElements;
    private final int totalPages;

    @JsonProperty("currentPage") // JSON 응답에서 `currentPage`를 1부터 시작하도록 변경
    private final int currentPage;

    public UserListResponseDto(Page<User> page) {
        Objects.requireNonNull(page, "페이지 객체가 null일 수 없습니다.");

        this.users = page.hasContent()
                ? page.getContent().stream().map(UserDetailResponseDto::from).collect(Collectors.toList())
                : Collections.emptyList();

        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber() + 1; // 0부터 시작하는 페이지 번호를 1부터 시작하도록 변경
    }
}
