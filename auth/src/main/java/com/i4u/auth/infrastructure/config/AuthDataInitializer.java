package com.i4u.auth.infrastructure.config;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.i4u.auth.domain.AuthUser;
import com.i4u.auth.domain.AuthUserRole;
import com.i4u.auth.domain.repository.AuthUserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthDataInitializer {

	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initUserData() {
		return args -> {

			// 이미 데이터가 있는지 확인
			if (authUserRepository.count() > 15) {
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

			List<AuthUser> authUsers = Arrays.asList(
					AuthUser.builder()
						.userId(hub1Uuid)
						.email("hub_manager1@i4u.com")
						.password(passwordEncoder.encode("password123!")) // 필요하면 암호화 적용
						.slackId("hub_manager1_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub2Uuid)
						.email("hub_manager2@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager2_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub3Uuid)
						.email("hub_manager3@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager3_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub4Uuid)
						.email("hub_manager4@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager4_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub5Uuid)
						.email("hub_manager5@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager5_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub6Uuid)
						.email("hub_manager6@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager6_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub7Uuid)
						.email("hub_manager7@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager7_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub8Uuid)
						.email("hub_manager8@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager8_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub9Uuid)
						.email("hub_manager9@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager9_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub10Uuid)
						.email("hub_manager10@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager10_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub11Uuid)
						.email("hub_manager11@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager11_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub12Uuid)
						.email("hub_manager12@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager12_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub13Uuid)
						.email("hub_manager13@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager13_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub14Uuid)
						.email("hub_manager14@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager14_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub15Uuid)
						.email("hub_manager15@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager15_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub16Uuid)
						.email("hub_manager16@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager16_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build(),
					AuthUser.builder()
						.userId(hub17Uuid)
						.email("hub_manager17@i4u.com")
						.password(passwordEncoder.encode("password123!"))
						.slackId("hub_manager17_slack")
						.role(AuthUserRole.HUB_MANAGER)
						.build()
				);

			authUserRepository.saveAll(authUsers);
			System.out.println("사용자 초기 데이터 로딩 완료");

		};

	}

}