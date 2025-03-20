package com.i4u.hub.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import com.i4u.hub.presentation.dtos.request.DeliveryHubCreateRequest;
import com.i4u.hub.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.hub.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.hub.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.hub.presentation.dtos.response.ShipperHubResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubClientService {

	private final HubRepository hubRepository;

	/**
	 * 허브 담당자가 관리하는 허브 ID 반환 (소속 검증)
	 *
	 * @param userId : 검증을 요청할 사용자
	 * @return : 허브 관리자의 허브 ID 반환
	 */
	public UUID confirmHubFromUser(UUID userId) {
		// findByHubManagerId(userId) 메서드 생성 필요
		// 담당 허브가 없거나 삭제됐다면 null을 반환하기
		return null;
	}

	/**
	 * 허브 검증 요청 (Shipper)
	 * 
	 * @param hubId : 검증할 허브 ID
	 * @return : 검증된 허브 내용
	 */
	public ShipperHubResponse confirmHubFromShipper(UUID hubId) {
		Hub hub = hubRepository.findById(hubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		ShipperHubResponse response = ShipperHubResponse.builder()
			.hubId(hub.getHubId()).isDeleted(false).build();

		return response;
	}

	/**
	 * 허브 검증 요청 (Delivery Create)
	 *
	 * @param recipientHubId : 수령(목적지) 허브
	 * @param supplierHubId : 출발(공급지) 허브
	 * @return : 검증한 내용
	 */
	public DeliveryHubCreateResponse confirmHubsFromDelivery(UUID recipientHubId, UUID supplierHubId) {
		// 목적지 허브 (업체로 가기 전 마지막 허브)
		Hub recipientHub = hubRepository.findById(recipientHubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		// 출발 허브 (물건을 공급할 허브)
		Hub supplierHub = hubRepository.findById(supplierHubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		return DeliveryHubCreateResponse.builder()
			.recipientHubId(recipientHub.getHubId()).supplierHubId(supplierHub.getHubId()).isDeleted(false)
			.build();
	}

	/**
	 * 허브 검증 요청 (Delivery Update)
	 *
	 * @param recipientHubId : 검증을 요청할 허브
	 * @return : 검증한 내용
	 */
	public DeliveryHubUpdateResponse updateConfirmHubsFromDelivery(UUID recipientHubId) {
		// 목적지 허브만 검증 (업체로 가기 전 마지막 허브)
		Hub recipientHub = hubRepository.findById(recipientHubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		return DeliveryHubUpdateResponse.builder()
			.arriveHubId(recipientHub.getHubId()).isDeleted(false)
			.build();
	}

	/**
	 * 허브 검증 요청 (Shipper -> Gateway)
	 * @param hubId : 검증할 허브 ID
	 * @return : 검증된 허브 내용
	 */
	public Map<String, Object> confirmHubFromShippers(UUID hubId) {
		// 허브 검증
		Hub hub = hubRepository.findById(hubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		Map<String, Object> hubResponse = new HashMap<>();
		hubResponse.put("hubId", hub.getHubId());
		// hubResponse.put("hubManagerId", hub.getHubManagerId());
		hubResponse.put("isDeleted", false);

		return hubResponse;
	}

}
