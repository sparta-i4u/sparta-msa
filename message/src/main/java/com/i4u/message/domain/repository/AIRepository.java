package com.i4u.message.domain.repository;

import com.i4u.message.domain.model.AI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIRepository extends JpaRepository<AI, UUID> {

}
