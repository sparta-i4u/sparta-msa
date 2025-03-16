package com.i4u.user.infrastructure.security;

import com.i4u.user.domain.User;
import com.i4u.user.domain.repository.UserRepository;
import com.i4u.user.application.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Slack ID(사용자 이름)로 사용자 정보를 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findBySlackIdAndIsDeletedFalse(username)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        return new UserDetailsImpl(user);
    }

    // UUID로 사용자 조회하는 메서드 추가
    public UserDetails loadUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        return new UserDetailsImpl(user);
    }
}
