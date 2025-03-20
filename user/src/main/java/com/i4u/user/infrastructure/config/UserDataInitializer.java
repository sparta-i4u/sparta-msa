package com.i4u.user.infrastructure.config;

import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import com.i4u.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserDataInitializer {

    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initUserData() {
        return args -> {
            // 마스터 관리자 생성
            User masterAdmin = User.createUser(
                    "master_admin",
                    "password123!",
                    "시스템 관리자",
                    "master@i4u.com",
                    "master_slack",
                    UserRole.MASTER
            );

            // 일반 관리자 생성
            List<User> admins = Arrays.asList(
                    User.createUser("hub_manager1", "password123!", "서울 관리자", "hub_manager1@i4u.com", "hub_manager1_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager2", "password123!", "경기북부 관리자", "hub_manager2@i4u.com", "hub_manager2_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager3", "password123!", "경기남부 관리자", "hub_manager3@i4u.com", "hub_manager3_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager4", "password123!", "부산 관리자", "hub_manager4@i4u.com", "hub_manager4_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager5", "password123!", "대구 관리자", "hub_manager5@i4u.com", "hub_manager5_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager6", "password123!", "인천 관리자", "hub_manager6@i4u.com", "hub_manager6_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager7", "password123!", "광주 관리자", "hub_manager7@i4u.com", "hub_manager7_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager8", "password123!", "대전 관리자", "hub_manager8@i4u.com", "hub_manager8_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager9", "password123!", "울산 관리자", "hub_manager9@i4u.com", "hub_manager9_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager10", "password123!", "세종 관리자", "hub_manager10@i4u.com", "hub_manager10_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager11", "password123!", "강원 관리자", "hub_manager11@i4u.com", "hub_manager11_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager12", "password123!", "충북 관리자", "hub_manager12@i4u.com", "hub_manager12_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager13", "password123!", "충남 관리자", "hub_manager13@i4u.com", "hub_manager13_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager14", "password123!", "전북 관리자", "hub_manager14@i4u.com", "hub_manager14_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager15", "password123!", "전남 관리자", "hub_manager15@i4u.com", "hub_manager15_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager16", "password123!", "경북 관리자", "hub_manager16@i4u.com", "hub_manager16_slack", UserRole.HUB_MANAGER),
                    User.createUser("hub_manager17", "password123!", "경남 관리자", "hub_manager17@i4u.com", "hub_manager17_slack", UserRole.HUB_MANAGER)
            );

            List<User> allUsers = new ArrayList<>();
            allUsers.add(masterAdmin);
            allUsers.addAll(admins);

            userRepository.saveAll(allUsers);

            System.out.println("사용자 초기 데이터 로딩 완료");
        };
    }
}
