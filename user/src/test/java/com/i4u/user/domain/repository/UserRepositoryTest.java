package com.i4u.user.domain.repository;

import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.createUser(
                "testUser", "password123", "nickname", "test@email.com", "slack123", UserRole.HUB_MANAGER
        );
        userRepository.save(testUser);
    }

    @Test
    void 사용자_저장_및_조회_성공() {
        // when
        Optional<User> foundUser = userRepository.findByUserIdAndIsDeletedFalse(testUser.getUserId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void 특정_Slack_ID로_사용자_조회_성공() {
        // when
        Optional<User> foundUser = userRepository.findBySlackIdAndIsDeletedFalse("slack123");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getSlackId()).isEqualTo("slack123");
    }

    @Test
    void 역할_기반_사용자_조회_성공() {
        // given
        User anotherUser = User.createUser(
                "adminUser", "password123", "Admin", "admin@email.com", "slack456", UserRole.MASTER
        );
        userRepository.save(anotherUser);

        // when
        List<User> hubManagers = userRepository.findByRoleAndIsDeletedFalse(UserRole.HUB_MANAGER);
        List<User> masters = userRepository.findByRoleAndIsDeletedFalse(UserRole.MASTER);

        // then
        assertThat(hubManagers).hasSize(1);
        assertThat(hubManagers.get(0).getUsername()).isEqualTo("testUser");

        assertThat(masters).hasSize(1);
        assertThat(masters.get(0).getUsername()).isEqualTo("adminUser");
    }

    @Test
    void 사용자_논리_삭제_성공() {
        // given
        testUser.softDelete(UUID.randomUUID());
        userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByUserIdAndIsDeletedFalse(testUser.getUserId());

        // then
        assertThat(foundUser).isEmpty(); // 논리 삭제된 사용자는 조회되지 않아야 함
    }

    @Test
    void 논리_삭제된_사용자는_조회되지_않음() {
        // given
        testUser.softDelete(UUID.randomUUID());
        userRepository.save(testUser);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = userRepository.findAllByIsDeletedFalse(pageable);

        // then
        assertThat(users.getContent()).doesNotContain(testUser); // Page에서 직접 검증
    }
}
