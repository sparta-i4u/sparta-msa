package com.i4u.user.application.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor
public class UserCreateRequestDto { // 회원가입 요청 DTO

    @NotBlank(message = "Username은 필수 입력값입니다.")
    @Size(min = 4, max = 10, message = "Username은 4자 이상 10자 이하로 입력해야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "Slack ID는 필수 입력값입니다.")
    private String slackId;

    @NotBlank(message = "역할(Role)은 필수 입력값입니다.")
    private String role;
}
