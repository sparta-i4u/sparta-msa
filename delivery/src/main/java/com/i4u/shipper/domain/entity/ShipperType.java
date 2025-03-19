package com.i4u.shipper.domain.entity;

import lombok.ToString;

@ToString
public enum ShipperType {
	HUB,     // 허브 → 허브 배송 담당자
	COMPANY  // 허브 → 업체 배송 담당자
}