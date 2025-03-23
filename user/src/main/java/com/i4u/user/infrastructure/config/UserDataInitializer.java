package com.i4u.user.infrastructure.config;

import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import com.i4u.user.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class UserDataInitializer {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUserData() {
        return args -> {


            // 이미 데이터가 있는지 확인
            if (userRepository.count() > 15) {
                System.out.println("사용자 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
                return;
            }

            UUID hub1Uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID hub2Uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
            UUID hub3Uuid = UUID.fromString("00000000-0000-0000-0000-000000000003");
            UUID hub4Uuid = UUID.fromString("00000000-0000-0000-0000-000000000004");
            UUID hub5Uuid = UUID.fromString("00000000-0000-0000-0000-000000000005");
            UUID hub6Uuid = UUID.fromString("00000000-0000-0000-0000-000000000006");
            UUID hub7Uuid = UUID.fromString("00000000-0000-0000-0000-000000000007");
            UUID hub8Uuid = UUID.fromString("00000000-0000-0000-0000-000000000008");
            UUID hub9Uuid = UUID.fromString("00000000-0000-0000-0000-000000000009");
            UUID hub10Uuid = UUID.fromString("00000000-0000-0000-0000-000000000010");
            UUID hub11Uuid = UUID.fromString("00000000-0000-0000-0000-000000000011");
            UUID hub12Uuid = UUID.fromString("00000000-0000-0000-0000-000000000012");
            UUID hub13Uuid = UUID.fromString("00000000-0000-0000-0000-000000000013");
            UUID hub14Uuid = UUID.fromString("00000000-0000-0000-0000-000000000014");
            UUID hub15Uuid = UUID.fromString("00000000-0000-0000-0000-000000000015");
            UUID hub16Uuid = UUID.fromString("00000000-0000-0000-0000-000000000016");
            UUID hub17Uuid = UUID.fromString("00000000-0000-0000-0000-000000000017");

            // 허브 관리자 생성
            List<User> users = Arrays.asList(
                    User.builder()
                            .userId(hub1Uuid)
                            .username("hub_manager1")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("서울 관리자")
                            .email("hub_manager1@i4u.com")
                            .slackId("hub_manager1_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub2Uuid)
                            .username("hub_manager2")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("경기북부 관리자")
                            .email("hub_manager2@i4u.com")
                            .slackId("hub_manager2_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub3Uuid)
                            .username("hub_manager3")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("경기남부 관리자")
                            .email("hub_manager3@i4u.com")
                            .slackId("hub_manager3_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub4Uuid)
                            .username("hub_manager4")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("부산 관리자")
                            .email("hub_manager4@i4u.com")
                            .slackId("hub_manager4_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub5Uuid)
                            .username("hub_manager5")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("대구 관리자")
                            .email("hub_manager5@i4u.com")
                            .slackId("hub_manager5_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub6Uuid)
                            .username("hub_manager6")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("인천 관리자")
                            .email("hub_manager6@i4u.com")
                            .slackId("hub_manager6_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub7Uuid)
                            .username("hub_manager7")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("광주 관리자")
                            .email("hub_manager7@i4u.com")
                            .slackId("hub_manager7_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub8Uuid)
                            .username("hub_manager8")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("대전 관리자")
                            .email("hub_manager8@i4u.com")
                            .slackId("hub_manager8_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub9Uuid)
                            .username("hub_manager9")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("울산 관리자")
                            .email("hub_manager9@i4u.com")
                            .slackId("hub_manager9_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub10Uuid)
                            .username("hub_manager10")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("세종 관리자")
                            .email("hub_manager10@i4u.com")
                            .slackId("hub_manager10_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub11Uuid)
                            .username("hub_manager11")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("강원 관리자")
                            .email("hub_manager11@i4u.com")
                            .slackId("hub_manager11_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub12Uuid)
                            .username("hub_manager12")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("충북 관리자")
                            .email("hub_manager12@i4u.com")
                            .slackId("hub_manager12_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub13Uuid)
                            .username("hub_manager13")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("충남 관리자")
                            .email("hub_manager13@i4u.com")
                            .slackId("hub_manager13_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub14Uuid)
                            .username("hub_manager14")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("전북 관리자")
                            .email("hub_manager14@i4u.com")
                            .slackId("hub_manager14_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub15Uuid)
                            .username("hub_manager15")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("전남 관리자")
                            .email("hub_manager15@i4u.com")
                            .slackId("hub_manager15_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub16Uuid)
                            .username("hub_manager16")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("경북 관리자")
                            .email("hub_manager16@i4u.com")
                            .slackId("hub_manager16_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build(),
                    User.builder()
                            .userId(hub17Uuid)
                            .username("hub_manager17")
                            .password(passwordEncoder.encode("password123!"))
                            .nickname("경남 관리자")
                            .email("hub_manager17@i4u.com")
                            .slackId("hub_manager17_slack")
                            .role(UserRole.HUB_MANAGER)
                            .build()
            );

            for (User user : users) {
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.execute(status -> {
                    try {
                        // 네이티브 SQL 쿼리 사용
                        String sql = "INSERT INTO p_user (user_id, username, password, nickname, email, slack_id, role, is_deleted, created_at, updated_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, false, now(), now())";

                        Query query = entityManager.createNativeQuery(sql);
                        query.setParameter(1, user.getUserId());
                        query.setParameter(2, user.getUsername());
                        query.setParameter(3, user.getPassword());
                        query.setParameter(4, user.getNickname());
                        query.setParameter(5, user.getEmail());
                        query.setParameter(6, user.getSlackId());
                        query.setParameter(7, user.getRole().name());

                        query.executeUpdate();
                        System.out.println("사용자 저장 완료: " + user.getUsername());
                    } catch (Exception e) {
                        System.err.println("사용자 저장 중 오류 발생: " + user.getUsername() + " - " + e.getMessage());
                        status.setRollbackOnly();
                    }
                    return null;
                });
            }

            System.out.println("사용자 초기 데이터 로딩 완료");
        };
    }
}
