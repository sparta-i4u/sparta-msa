package com.i4u.delivery.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.i4u.delivery.domain.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryRepositoryCustom {
}