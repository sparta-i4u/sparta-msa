package com.i4u.shipper.domain.entity;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.i4u.common.entity.Basic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_shipper")
@SQLRestriction("is_deleted IS NULL OR is_deleted = false")
public class Shipper extends Basic {

	// 배송 담당자 ID
	@Id
	private UUID shipperId;

	// 소속 허브 ID
	@Column(name = "hub_id")
	private UUID hubId;

	// 배송 담당자의 Type
	@Column(name = "shipper_type")
	private ShipperType shipperType;

	// 배송 순서
	@Column(name = "shipper_order")
	private Integer shipperOrder;

	// 사용자 ID
	@Column(name = "user_id")
	private UUID userId;

	// 사용자의 Slack ID
	@Column(name = "user_slack_id")
	private String userSlackId;

	/**
	 * 배송 담당자 수정
	 * @param updateShipper : 수정할 배송 담담자 정보
	 */
	public void updateShipper(Shipper updateShipper) {
		Optional.ofNullable(updateShipper.getHubId()).ifPresent(hubId -> this.hubId = hubId);
		Optional.ofNullable(updateShipper.getShipperOrder()).ifPresent(shipperOrder -> this.shipperOrder = shipperOrder);
		Optional.ofNullable(updateShipper.getShipperType()).ifPresent(shipperType -> this.shipperType = shipperType);
	}

}
