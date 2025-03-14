package com.i4u.shipper.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.i4u.shipper.domain.entity.Shipper;

public interface ShipperRepository extends JpaRepository<Shipper, UUID>, ShipperRepositoryCustom {
}