package com.i4u.user.application;

import com.i4u.user.application.dtos.request.UserRequestDto;
import com.i4u.user.application.dtos.response.UserResponseDto;
import com.i4u.user.domain.User;
import com.i4u.user.application.exception.UserException;
import com.i4u.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    public UserResponseDto createUser(UserRequestDto requestDTO) {
        // Slack ID 중복 체크
        if (userRepository.findBySlackId(requestDTO.getSlackId()).isPresent()) {
            throw new UserException(UserException.UserErrorType.DUPLICATE_USERNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        // User 엔티티 생성
        User newUser = requestDTO.toEntity(encodedPassword);
        userRepository.save(newUser);
        return UserResponseDto.from(newUser);
    }

    // 특정 사용자 조회 - ID
    public Optional<UserResponseDto> getUserById(Long userId) {
        return userRepository.findByUserIdAndIsDeletedFalse(userId)
                .map(UserResponseDto::from);
    }

    // 특정 사용자 조회 - Slack ID
    public Optional<UserResponseDto> getUserBySlackId(String slackId) {
        return userRepository.findBySlackId(slackId)
                .map(UserResponseDto::from);
    }

    // 전체 사용자 조회 (Soft Delete 적용) + 페이징 적용
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAllByIsDeletedFalse(pageable)
                .map(UserResponseDto::from);
    }

    // 사용자 정보 업데이트
    public UserResponseDto updateUser(Long userId, UserResponseDto requestDTO) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        user.updateUser(requestDTO.getNickname(), requestDTO.getEmail());
        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    // 사용자 논리 삭제 (Soft Delete)
    public void deleteUser(Long userId, UUID deletedBy) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        user.softDelete(deletedBy);
        userRepository.save(user);
    }
}
