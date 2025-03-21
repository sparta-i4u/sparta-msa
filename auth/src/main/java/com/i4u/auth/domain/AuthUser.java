package com.i4u.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA만 사용 가능
@Builder
@Table(name = "auth_user", uniqueConstraints = {@UniqueConstraint(name = "unique_email", columnNames = "email")})
public class AuthUser {

    @Id
    private UUID userId; // User 서비스에서 전달받은 사용자 ID 저장

    @Column(nullable = false, unique = true, length = 255)
    private String email; // 로그인 이메일

    @Column(nullable = false, length = 255)
    private String password; // 암호화된 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthUserRole role; // 인증된 사용자 역할

    @Column(nullable = false, unique = true, length = 100)
    private String slackId; // Slack ID 추가

    @Builder.Default  // ✅ 추가된 부분
    @Column(nullable = false)
    private boolean isDeleted = false;  // 삭제 여부 (기본값: false)

    // ✅ 정적 팩토리 메서드 - 회원가입 시 계정 생성 (`userId` 추가)
    public static AuthUser createAuthUser(UUID userId, String email, String rawPassword, String slackId, AuthUserRole role, BCryptPasswordEncoder encoder) {
        return AuthUser.builder()
                .userId(userId) // User 서비스에서 받은 `userId` 저장
                .email(email)
                .password(encoder.encode(rawPassword)) // 비밀번호 암호화 저장
                .role(role)
                .slackId(slackId)
                .isDeleted(false)
                .build();
    }

    // ✅ 비밀번호 변경
    public void updatePassword(String newRawPassword, BCryptPasswordEncoder encoder) {
        this.password = encoder.encode(newRawPassword);
    }
}
