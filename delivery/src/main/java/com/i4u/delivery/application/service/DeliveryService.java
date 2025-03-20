package com.i4u.delivery.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.i4u.client.AuthClient;
import com.i4u.client.HubClient;
import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;
import com.i4u.delivery.application.dtos.request.DeliveryStatusUpdateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryUpdateRequest;
import com.i4u.delivery.application.dtos.response.DeliveryCreateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetOneResponse;
import com.i4u.delivery.application.dtos.response.DeliveryStateUpdateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryUpdateResponse;
import com.i4u.delivery.application.exception.DeliveryException;
import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.client.ShipperClient;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryUserSlackIdRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryUserUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserSlackIdResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserUpdateResponse;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;
	private final HubClient hubClient;
	private final AuthClient authClient;
	private final ShipperClient shipperClient;
	private final OrderClient orderClient;

	/**
	 * 배송 생성
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */ // MASTER (주문에서 생성 요청이 넘어오면 받아줄 포인트)
	public DeliveryCreateResponse createDelivery(DeliveryCreateRequest request) {
		// 1. [hubClient] 허브 검증
		ResponseEntity<CommonResponse<DeliveryHubCreateResponse>> responseHub = hubClient.confirmHubsFromDelivery(
			request.getDepartHubId(), request.getArriveHubId() );

		if (responseHub.getBody().getData().getIsDeleted()) {
			throw new DeliveryException("배송할 수 있는 허브가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. [userClient] 수령인 슬랙 ID 받아오기
		ConfirmUserResponse responseUser = authClient.confirmUser(request.getRecipientId());

		if (responseUser.getIsDeleted()) {
			throw new DeliveryException("수령인이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. [shipperClient]  배송 담당자 배정 요청
		ResponseEntity<CommonResponse<DeliveryShipperResponse>> responseShipper = shipperClient.assignShipper(
			DeliveryShipperRequest.builder()
			.recipientHubId(request.getArriveHubId()).build());

		if (responseShipper.getBody().getData().getIsDeleted()) {
			throw new DeliveryException("배송 가능한 배송 담당자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. 배송 생성
		UUID shipperId = responseShipper.getBody().getData().getShipperId();
		String recipientSlackId = responseUser.getUserSlackId();

		Delivery delivery = request.toDelivery(DeliveryState.PREPARING, recipientSlackId, shipperId);
		Delivery savedDelivery = deliveryRepository.save(delivery);

		return DeliveryCreateResponse.fromDelivery(savedDelivery);
	}

	/**
	 * 배송 전체 조회
	 *
	 * @return : 조회한 전체 배송 내용
	 */   // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER
	public PagedModel<DeliveryGetListResponse> getAllDeliveries(Pageable pageable, DeliverySearchRequest request,
		String userId, String role) {
		// 1. 사용자 권한 값 넘겨주기 (전체 조회 제한 여부 확인)
		UUID hubManagerHubId = null;
		if (role.equals("ROLE_HUB_MANAGER")) {
			hubManagerHubId = hubClient.confirmHubFromUser(UUID.fromString(userId));
		}

		PagedModel<DeliveryGetListResponse> deliveryList = deliveryRepository.searchDeliveries(pageable, request, userId, role, hubManagerHubId);
		return deliveryList;
	}

	/**
	 * 배송 단건 조회
	 *
	 * @param deliveryId : 조회할 배송 ID
	 * @param userId
	 * @param role
	 * @return : 조회한 배송 내용
	 */   // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER
	public DeliveryGetOneResponse getOneDelivery(UUID deliveryId, String userId, String role) {
		Delivery delivery = findDelivery(deliveryId);

		// 1. 해당 배송을 조회할 수 있는 사용자인지 확인
		if ( (role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), delivery.getArriveHubId(), delivery.getDepartHubId())) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( role.equals("DELIVERY_MANAGER") &&
			!delivery.getShipperId().equals(userId) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( !role.equals("ROLE_MASTER") || !role.equals("ROLE_HUB_MANAGER") || !role.equals("DELIVERY_MANAGER") ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		return DeliveryGetOneResponse.fromDelivery(delivery);
	}

	/**
	 * 배송 수정 (상태를 제외한 정보 수정)
	 *
	 * @param deliveryId : 수정할 배송 ID
	 * @param request    : 수정할 배송 정보
	 * @param userId
	 * @param role
	 * @return : 수정한 배송 내용
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 배송) - shipperId와 일치하면 됨
	@Transactional
	public DeliveryUpdateResponse updateDelivery(UUID deliveryId, DeliveryUpdateRequest request, String userId,
		String role) {
		Delivery delivery = findDelivery(deliveryId);

		// 1. 해당 배송을 수정할 수 있는 사용자인지 확인
		if ( (role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), delivery.getArriveHubId(), delivery.getDepartHubId())) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( role.equals("DELIVERY_MANAGER") &&
			!delivery.getShipperId().equals(userId) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( !role.equals("ROLE_MASTER") || !role.equals("ROLE_HUB_MANAGER") || !role.equals("DELIVERY_MANAGER") ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. 타 도메인으로 요청 후 검증이 필요한 수정 사항이 있는지 확인하기 - 수령인과 주소 변경 가능
		UUID recipientHubId = delivery.getArriveHubId();
		UUID shipperId = delivery.getShipperId();

		if (!delivery.getArriveHubId().equals(request.getArriveHubId())) {
			// 2-1. [hubClient] 허브 ID가 다른 경우에만 Hub로 요청 전송
			ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> responseHub = hubClient.updateConfirmHubsFromDelivery(
				request.getArriveHubId());

			if (responseHub.getBody().getData().getIsDeleted()) {
				throw new DeliveryException("허브가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 2-2. [shipperClient] 허브 바뀌었으니까 배송 담당자 다시 주세요
			recipientHubId = responseHub.getBody().getData().getArriveHubId();

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
		if (!delivery.getRecipientId().equals(request.getRecipientId())) {
			// userID가 기존과 다른 경우만 요청 전송
			ConfirmUserResponse responseUser = authClient.confirmUser(request.getRecipientId());

			userSlackId = responseUser.getUserSlackId();
		}
		
		// 3. 수정하기
		Delivery updatingDelivery = request.toDelivery(recipientHubId, shipperId, userSlackId);
		delivery.updateDelivery(updatingDelivery);
		
		return DeliveryUpdateResponse.fromDelivery(delivery);
	}

	/**
	 * 배송 상태 수정 (상태만 수정)
	 *
	 * @param deliveryId : 상태를 수정할 배송 ID
	 * @param request    : 수정할 배송 상태
	 * @param userId
	 * @param role
	 * @return : 상태를 수정한 배송 정보
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 배송)
	@Transactional
	public DeliveryStateUpdateResponse updateDeliveryState(UUID deliveryId, DeliveryStatusUpdateRequest request,
		String userId, String role) {
		Delivery delivery = findDelivery(deliveryId);

		// 1. 해당 배송을 수정할 수 있는 사용자인지 확인
		if ( (role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), delivery.getArriveHubId(), delivery.getDepartHubId())) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( role.equals("DELIVERY_MANAGER") &&
			!delivery.getShipperId().equals(userId) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if ( !role.equals("ROLE_MASTER") || !role.equals("ROLE_HUB_MANAGER") || !role.equals("DELIVERY_MANAGER") ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

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
	 * @param userId
	 * @param role
	 */
	@Transactional // MASTER, HUB_MANAGER(담당 허브)
	public void deleteDelivery(UUID deliveryId, String userId, String role) {
		// 1. 배송 검색
		Delivery delivery = findDelivery(deliveryId);
		
		// 2. 사용자 권한 확인
		if ( (role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), delivery.getArriveHubId(), delivery.getDepartHubId())) ) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		if (! (role.equals("ROLE_MASTER") || role.equals("ROLE_HUB_MANAGER"))) {
			throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		// 3. delivery 삭제 로직 추가 - delivery 상태에 따라서 배송이 시작되었다면 삭제가 불가능하도록 설정하기
		if ( (delivery.getDeliveryState().equals(DeliveryState.PENDING) ||
			delivery.getDeliveryState().equals(DeliveryState.PREPARING)) ) {
			throw new DeliveryException("배송을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 삭제 처리
		delivery.softDelete(UUID.fromString(userId));

		// 5. [orderClient] order 쪽으로도 요청 전송
		orderClient.notificationDeliveryState(DeliveryOrderStateUpdateRequest.builder()
			.orderId(delivery.getOrderId()).deliveryId(delivery.getDeliveryId())
			.deliveryState(DeliveryState.DELETED.toString()).build());
	}

	/**
	 * 허브 관리자의 경우 관리하는 허브 ID와 일치하는지 확인하는 메서드
	 *
	 * @param userId         : 허브 매니저의 ID
	 * @param recipientHubId : 도착한 허브 ID
	 * @param supplierHubId  : 출발한 허브 ID
	 * @return : 일치 여부 반환
	 */
	private Boolean confirmHubId(UUID userId, UUID recipientHubId, UUID supplierHubId) {
		UUID hubManagerHubId = hubClient.confirmHubFromUser(userId);

		if (!(hubManagerHubId.equals(recipientHubId) || hubManagerHubId.equals(supplierHubId))) {
			return false;
		}

		return true;
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
