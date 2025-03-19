package com.i4u.delivery.application.service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryStatusUpdateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryUpdateRequest;
import com.i4u.delivery.application.dtos.response.DeliveryCreateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetOneResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import com.i4u.delivery.application.dtos.response.DeliveryStateUpdateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryUpdateResponse;
import com.i4u.delivery.application.exception.DeliveryException;
import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.client.HubClient;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.client.ShipperClient;
import com.i4u.delivery.presentation.client.UserClient;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderDeleteRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;
	private final HubClient hubClient;
	private final UserClient userClient;
	private final ShipperClient shipperClient;
	private final OrderClient orderClient;

	/**
	 * 배송 생성
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */
	public DeliveryCreateResponse createDelivery(DeliveryCreateRequest request) {
		// 1. 사용자 검증 (userId, Role은 order 측에서 @Header에 담아서 넘겨줄 예정)
		// {사용자 역할에 따라 배송 생성이 가능한지 (order로부터 client 요청을 받을 예정 (Endpoint처리) )}

		// 2. [hubClient] 허브 검증
		// DeliveryHubCreateResponse responseHub = hubClient.confirmHubs(DeliveryHubCreateRequest.builder()
		// 	.supplierHubId(request.getDepartHubId()).recipientHubId(request.getArriveHubId()).build());
		//
		// if (responseHub.getIsDeleted()) {
		// 	throw new DeliveryException("배송할 수 있는 허브가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }
		//
		// // 3. [userClient] 수령인 슬랙 ID 받아오기
		// DeliveryUserSlackIdResponse responseUser = userClient.confirmUser(DeliveryUserSlackIdRequest.builder()
		// 	.userId(request.getRecipientId()).build());
		//
		// if (responseUser.getIsDeleted()) {
		// 	throw new DeliveryException("수령인이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }
		//
		// 4. [shipperClient]  배송 담당자 배정 요청
		ResponseEntity<CommonResponse<DeliveryShipperResponse>> responseShipper = shipperClient.assignShipper(
			DeliveryShipperRequest.builder()
			.recipientHubId(request.getArriveHubId()).build());

		if (responseShipper.getBody().getData().getIsDeleted()) {
			throw new DeliveryException("배송 가능한 배송 담당자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. 배송 생성
		// UUID shipperId = responseShipper.getBody().getData().getShipperId();
		// String recipientSlackId = responseUser.getUserSlackId();

		UUID shipperId = UUID.randomUUID();
		String recipientSlackId = "slackId";

		Delivery delivery = request.toDelivery(DeliveryState.PREPARING, recipientSlackId, shipperId);
		Delivery savedDelivery = deliveryRepository.save(delivery);

		return DeliveryCreateResponse.fromDelivery(savedDelivery);
	}

	/**
	 * 배송 전체 조회
	 *
	 * @return : 조회한 전체 배송 내용
	 */
	public PagedModel<DeliveryGetListResponse> getAllDeliveries(Pageable pageable, DeliverySearchRequest request) {
		// 1. 사용자 권한 값 넘겨주기 (전체 조회 제한 여부 확인)
		PagedModel<DeliveryGetListResponse> deliveryList = deliveryRepository.searchDeliveries(pageable, request);
		return deliveryList;
	}

	/**
	 * 배송 단건 조회
	 *
	 * @param deliveryId : 조회할 배송 ID
	 * @return : 조회한 배송 내용
	 */
	public DeliveryGetOneResponse getOneDelivery(UUID deliveryId) {
		Delivery delivery = findDelivery(deliveryId);
		// 1. 해당 배송을 조회할 수 있는 사용자인지 확인
		return DeliveryGetOneResponse.fromDelivery(delivery);
	}

	/**
	 * 배송 수정 (상태를 제외한 정보 수정)
	 *
	 * @param deliveryId : 수정할 배송 ID
	 * @param request : 수정할 배송 정보
	 * @return : 수정한 배송 내용
	 */
	@Transactional
	public DeliveryUpdateResponse updateDelivery(UUID deliveryId, DeliveryUpdateRequest request) {
		Delivery delivery = findDelivery(deliveryId);

		// 1. 해당 배송을 수정할 수 있는 사용자인지 확인
		
		// 2. 타 도메인으로 요청 후 검증이 필요한 수정 사항이 있는지 확인하기 - 수령인과 주소 변경 가능
		UUID recipientHubId = delivery.getArriveHubId();
		UUID shipperId = delivery.getShipperId();

		if (!delivery.getArriveHubId().equals(request.getArriveHubId())) {
			// 2-1. [hubClient] 허브 ID가 다른 경우에만 Hub로 요청 전송
			DeliveryHubUpdateResponse responseHub = hubClient.updateHubInfo(DeliveryHubUpdateRequest.builder()
				.address(delivery.getAddress()).build());

			// 2-2. [shipperClient] 허브 바뀌었으니까 배송 담당자 다시 주세요
			recipientHubId = responseHub.getArriveHubId();

			ResponseEntity<CommonResponse<DeliveryShipperResponse>> responseShipper = shipperClient.assignShipper(
				DeliveryShipperRequest.builder()
				.recipientHubId(request.getArriveHubId()).build());

			if (responseShipper.getBody().getData().getIsDeleted()) {
				throw new DeliveryException("배송 가능한 배송 담당자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			shipperId = responseShipper.getBody().getData().getShipperId();

		}
		
		// 2-3. [userClient] 사용자 정보가 바뀌면 (SlackId만 변경하는 경우 -> 요청 없이 변경하도록 설정)
		String userSlackId = request.getRecipientSlackId();
		// if (!delivery.getRecipientId().equals(request.getRecipientId())) {
		// 	// userID가 기존과 다른 경우만 요청 전송
		// 	DeliveryUserUpdateResponse responseUser = userClient.updateUserInfo(DeliveryUserUpdateRequest.builder()
		// 		.userId(request.getRecipientId()).build());
		//
		// 	userSlackId = responseUser.getUserSlackId();
		// }
		
		// 3. 수정하기
		Delivery updatingDelivery = request.toDelivery(recipientHubId, shipperId, userSlackId);
		delivery.updateDelivery(updatingDelivery);
		
		return DeliveryUpdateResponse.fromDelivery(delivery);
	}

	/**
	 * 배송 상태 수정 (상태만 수정)
	 * 
	 * @param deliveryId : 상태를 수정할 배송 ID
	 * @param request : 수정할 배송 상태
	 * @return : 상태를 수정한 배송 정보
	 */
	@Transactional
	public DeliveryStateUpdateResponse updateDeliveryState(UUID deliveryId, DeliveryStatusUpdateRequest request) {
		Delivery delivery = findDelivery(deliveryId);

		// 1. 해당 배송을 수정할 수 있는 사용자인지 확인

		// 2. 배송 상태 수정하기
		Delivery updatingDelivery = request.toDelivery();
		delivery.updateDeliveryState(updatingDelivery);

		// 3. 변경된 상태에 따라서 order 측으로 요청 전송
		orderClient.notificationDeliveryState(DeliveryOrderStateUpdateRequest.builder()
			.orderId(delivery.getOrderId()).deliveryId(delivery.getDeliveryId())
			.deliveryState(delivery.getDeliveryState().toString()).build());

		// 4. 상태가 변경된 배송 정보 반환
		return DeliveryStateUpdateResponse.fromDelivery(delivery);
	}

	/**
	 * 배송 삭제
	 *
	 * @param deliveryId : 삭제할 배송 ID
	 */
	@Transactional
	public void deleteDelivery(UUID deliveryId) {
		// 1. 배송 검색
		Delivery delivery = findDelivery(deliveryId);
		
		// 2. 사용자 권한 확인
		
		// 3. delivery 삭제 로직 추가 - delivery 상태에 따라서 배송이 시작되었다면 삭제가 불가능하도록 설정하기
		if ( (delivery.getDeliveryState().equals(DeliveryState.PENDING) ||
			delivery.getDeliveryState().equals(DeliveryState.PREPARING)) ) {
			throw new DeliveryException("배송을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// delivery.softDelete(/*사용자 정보*/);

		// 4. [orderClient] order 쪽으로도 요청 전송
		orderClient.notificationDeliveryState(DeliveryOrderStateUpdateRequest.builder()
			.orderId(delivery.getOrderId()).deliveryId(delivery.getDeliveryId())
			.deliveryState(DeliveryState.DELETED.toString()).build());
	}


	/**
	 * 배송 내역 탐색
	 *
	 * @param deliveryId : 탐색할 배송 ID
	 * @return : 배송 내역
	 */
	private Delivery findDelivery(UUID deliveryId) {
		return deliveryRepository.findById(deliveryId)
			.orElseThrow(() -> new DeliveryException("해당 배송 내역을 찾을 수 없습니다", HttpStatus.BAD_REQUEST));
	}
}
