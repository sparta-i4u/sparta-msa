package com.i4u.hub.domain.repository;

import com.i4u.hub.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {
    // 허브 ID로 허브 조회
    Optional<Hub> findById(UUID hubId);
    // 허브 목록 조회
    List<Hub> findAll();
    // 허브 삭제
    void deleteById(UUID hubId);
}
