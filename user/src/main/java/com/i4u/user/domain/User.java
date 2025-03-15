package com.i4u.user.domain;

import com.i4u.common.entity.Basic;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA만 사용 가능
@Builder(access = AccessLevel.PRIVATE) // 빌더 직접 사용 방지
@Table(name = "p_user", uniqueConstraints = {@UniqueConstraint(name = "unique_email", columnNames = "email")})
public class User extends Basic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", columnDefinition = "Long", updatable = false, nullable = false)
    private Long userId;

    @Column(name="username", nullable = false, length = 100, unique = true)
    private String username; // 사용자 ID (최소 4자, 최대 10자)

    @Column(name="password", nullable = false, length = 255)
    private String password; // 비밀번호 (BCrypt 암호화 필요)

    @Column(name="nickname", nullable = false, length = 255)
    private String nickname; // 사용자 닉네임

    @Column(name="email", nullable = false, length = 255, unique = true)
    private String email; // 사용자 이메일 (Unique)

    @Column(name="slack_id", nullable = false, length = 100, unique = true)
    private String slackId; // Slack ID 관리

    @Enumerated(EnumType.STRING) // Enum을 String으로 저장
    @Column(name="role", nullable = false)
    private UserRole role; // 사용자 역할 (CUSTOMER, OWNER, MANAGER, MASTER)

    // 정적 팩토리 메서드 - User 생성
    public static User createUser(String username, String password, String nickname, String email, String slackId, UserRole role) {
        return User.builder()
                .username(username)
                .password(password) // Service에서 암호화 후 주입
                .nickname(nickname)
                .email(email)
                .slackId(slackId)
                .role(role)
                .build();
    }

    // 사용자 정보 업데이트
    public void updateUser(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
