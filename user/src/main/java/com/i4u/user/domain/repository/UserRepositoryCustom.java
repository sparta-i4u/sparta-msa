package com.i4u.user.domain.repository;

import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> searchUsers(String keyword, UserRole role, Pageable pageable, boolean includeDeleted);
}
