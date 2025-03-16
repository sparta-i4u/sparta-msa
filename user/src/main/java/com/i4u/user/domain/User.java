package com.i4u.user.domain;

import com.i4u.common.entity.Basic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA만 사용 가능
@Builder(access = AccessLevel.PRIVATE) // 빌더 직접 사용 방지
@Table(name = "p_user", uniqueConstraints = {@UniqueConstraint(name = "unique_email", columnNames = "email")})
public class User extends Basic {

    @Id
    @GeneratedValue(generator = "UUID") //(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="user_id", columnDefinition = "Long", updatable = false, nullable = false)
    private UUID userId;

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

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private UserRole role; // 사용자 역할 (CUSTOMER 제거)

    // 정적 팩토리 메서드 - User 생성 (회원가입 시 역할 선택 가능)
    public static User createUser(String username, String password, String nickname, String email, String slackId, UserRole role) {
        return User.builder()
                .userId(UUID.randomUUID())
                .username(username)
                .password(password) // Service에서 암호화 후 주입
                .nickname(nickname)
                .email(email)
                .slackId(slackId)
                .role(role)
                .build();
    }

    // 사용자 정보 수정 (본인 또는 관리자만 가능)
    public void updateUser(String nickname, String email, Long requesterId, boolean isAdmin) {
        if (!this.userId.equals(requesterId) && !isAdmin) {
            throw new IllegalStateException("본인 또는 관리자만 수정할 수 있습니다.");
        }
        this.nickname = nickname;
        this.email = email;
    }

    // 사용자 역할 변경 (MASTER만 가능)
    public void updateRole(UserRole newRole, UserRole adminRole) {
        if (!adminRole.isMaster()) {
            throw new IllegalStateException("MASTER 권한이 있어야 역할을 변경할 수 있습니다.");
        }
        this.role = newRole;
    }
}
