package com.i4u.user.application.dtos.request;

import com.i4u.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto { // 사용자 정보 수정 DTO

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    private String role; // 역할 변경 가능하도록 필드 추가

    // 기존 값을 유지하면서 변경된 값만 적용하는 정적 팩토리 메서드
    public static UserUpdateRequestDto createUpdatedDto(User user, UserUpdateRequestDto newDto) {
        return UserUpdateRequestDto.builder()
                .nickname(newDto.nickname != null && !newDto.nickname.isBlank() ? newDto.nickname : user.getNickname())
                .email(newDto.email != null && !newDto.email.isBlank() ? newDto.email : user.getEmail())
                .role(newDto.role != null && !newDto.role.isBlank() ? newDto.role : user.getRole().name()) // 역할 변경 추가
                .build();
    }
}
