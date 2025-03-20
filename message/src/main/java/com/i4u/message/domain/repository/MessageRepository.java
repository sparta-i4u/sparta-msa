package com.i4u.message.domain.repository;

import com.i4u.message.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

}
