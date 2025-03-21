package com.i4u.shipper.application.dtos.request;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;

import com.i4u.shipper.domain.entity.ShipperType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipperSearchRequest {

	// 배송 담당자 타입
	private ShipperType shipperType;

	// 배송 담당자의 사용자명
	private UUID userId;

	// 배송 담당자가 속한 허브
	private UUID hubId;

	// 배송 순서 최솟값
	private Integer minShipperOrder;

	// 배송 순서 최댓값
	private Integer maxShipperOrder;

}
