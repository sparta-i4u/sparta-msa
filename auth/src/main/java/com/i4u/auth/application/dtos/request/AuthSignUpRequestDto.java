package com.i4u.auth.application.dtos.request;

import com.i4u.auth.domain.AuthUserRole;
import com.i4u.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignUpRequestDto {

    @NotBlank(message = "Username은 필수 입력값입니다.")
    @Size(min = 4, max = 10, message = "Username은 4자 이상 10자 이하로 입력해야 합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @NotBlank(message = "Slack ID는 필수 입력값입니다.")
    private String slackId;

    private AuthUserRole role; // AuthUserRole 사용

    //AuthUserRole을 UserRole로 변환하는 메서드
    public UserRole toUserRole() {
        return (role != null) ? UserRole.valueOf(role.name()) : UserRole.MASTER;
    }
}
