//package com.i4u.user.infrastructure.security.util;
//
//import com.i4u.user.infrastructure.security.UserDetailsImpl;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public class SecurityUtil {
//
//    // 현재 로그인한 사용자의 ID 가져오기
//    public static Optional<UUID> getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            return Optional.empty();
//        }
//
//        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
//            return Optional.of(userDetails.getUserId());
//        }
//
//        return Optional.empty();
//    }
//
//    // 현재 로그인한 사용자의 역할 가져오기
//    public static Optional<com.i4u.user.domain.UserRole> getCurrentUserRole() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            return Optional.empty();
//        }
//
//        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
//            return Optional.of(userDetails.getRole());
//        }
//
//        return Optional.empty();
//    }
//}
