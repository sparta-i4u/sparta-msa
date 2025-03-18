package com.i4u.hub.domain.repository;

import com.i4u.hub.domain.model.HubConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubConnectionRepository extends JpaRepository<HubConnection, UUID> {

}
