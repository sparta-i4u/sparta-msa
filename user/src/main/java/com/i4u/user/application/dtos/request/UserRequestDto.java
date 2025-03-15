package com.i4u.user.application.dtos.request;

import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Builder
public class UserRequestDto {

    @NotBlank(message = "Username은 필수 입력값입니다.")
    @Size(min = 4, max = 10, message = "Username은 4자 이상 10자 이하로 입력해야 합니다.")
    private final String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해야 합니다.")
    private final String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private final String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private final String email;

    @NotBlank(message = "Slack ID는 필수 입력값입니다.")
    private final String slackId;

    @NotBlank(message = "User Role은 필수 입력값입니다.")
    private final String role;

    // 정적 팩토리 메서드 - DTO를 User 엔티티로 변환
    public User toEntity() {
        return User.createUser(
                this.username,
                this.password,
                this.nickname,
                this.email,
                this.slackId,
                UserRole.valueOf(this.role.toUpperCase()) // Enum 변환
        );
    }
}
