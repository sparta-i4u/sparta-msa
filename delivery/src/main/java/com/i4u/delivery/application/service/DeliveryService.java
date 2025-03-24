package com.i4u.delivery.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
import com.i4u.delivery.presentation.client.MessageClient;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.client.ProductClient;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;
import com.i4u.delivery.presentation.dtos.response.OrderProductResponse;
import com.i4u.shipper.application.service.ShipperClient;
import com.i4u.shipper.presentation.dtos.request.MessageRequest;
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
	private final OrderClient orderClient;
	private final ShipperClient shipperClient;
	private final MessageClient messageClient;
	private final ProductClient productClient;

	private final RabbitTemplate rabbitTemplate;

	@Value("${i4u.err.queue.order}")
	private String orderErrorQueue;

	/**
	 * 배송 재생성 요청
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */ // MASTER (주문에서 생성 요청이 넘어오면 받아줄 포인트)
	public DeliveryCreateResponse createDelivery(DeliveryCreateRequest request) {
		try {
			// 1. [hubClient] 허브 검증 (출발 허브, 도착 허브 검증)
			System.out.println("supplier: " + request.getSupplierHubId());
			System.out.println("recipient: " + request.getRecipientHubId());
			DeliveryHubCreateResponse responseHub = hubClient.confirmHubsFromDelivery(
				request.getSupplierHubId(), request.getRecipientHubId() );

			if (responseHub.getIsDeleted()) {
				throw new DeliveryException("배송할 수 있는 허브가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 2. [productClient] 상품 재확인 요청 전송
			OrderProductResponse responseProduct = productClient.confirmProduct(request.getProductId(), request.getProductQuantity());

			if (responseProduct.getIsDeleted()) {
				throw new DeliveryException("상품의 재고가 없어 배송할 수 없습니다.", HttpStatus.BAD_REQUEST);
			}

			// 3. [userClient] 수령인 슬랙 ID 받아오기
			System.out.println("수령인 ID : " + request.getRecipientId());
			ConfirmUserResponse responseUser = authClient.confirmUser(request.getRecipientId());

			if (responseUser.getIsDeleted()) {
				throw new DeliveryException("수령인이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 4. [shipperClient]  배송 담당자 배정 요청
			System.out.println("recipientHubId From DeliveryService: " + request.getRecipientHubId());
			DeliveryShipperResponse responseShipper = shipperClient.assignShipper(
				request.getRecipientHubId());

			if (responseShipper.getIsDeleted()) {
				throw new DeliveryException("배송 가능한 배송 담당자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 5. 배송 생성
			UUID shipperId = responseShipper.getShipperId();
			String recipientSlackId = responseUser.getUserSlackId();

			Delivery delivery = request.toDelivery(DeliveryState.PREPARING, recipientSlackId, shipperId);
			Delivery savedDelivery = deliveryRepository.save(delivery);

			DeliveryCreateResponse response = DeliveryCreateResponse.fromDelivery(savedDelivery);

			// 6. 메세지 전송 요청 보내기
			// sendMessage(delivery, responseShipper, responseUser, request);

			return response;
		} catch (Exception e) {
			return null;
		}
	}

	private void sendMessage(Delivery delivery, DeliveryShipperResponse shipper,
		ConfirmUserResponse recipient, DeliveryCreateRequest request) {
		MessageRequest message = MessageRequest.builder()
			.orderId(delivery.getOrderId())
			.recipientEmail(recipient.getEmail())
			.recipientSlackId(recipient.getUserSlackId())
			.productName(request.getProductName())
			.productQuantity(request.getProductQuantity())
			.requirement(request.getRequirement())
			.supplierHubId(delivery.getDepartHubId())
			.recipientHubId(delivery.getArriveHubId())
			.shipperEmail(shipper.getShipperEmail())
			.shipperSlackId(shipper.getShipperSlackId())
			.build();

		messageClient.sendInfoToMessage(message);
	}

	/**
	 * 배송 전체 조회
	 *
	 * @return : 조회한 전체 배송 내용
	 */   // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER
	public PagedModel<DeliveryGetListResponse> getAllDeliveries(Pageable pageable, DeliverySearchRequest request,
																UUID userId, String role) {
		// 1. 사용자 권한 값 넘겨주기 (전체 조회 제한 여부 확인)
		UUID hubManagerHubId = null;
		if (role.equals("ROLE_HUB_MANAGER")) {
			hubManagerHubId = hubClient.confirmHubFromUser(userId);
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
	public DeliveryGetOneResponse getOneDelivery(UUID deliveryId, UUID userId, String role) {
		// 1. 배송 조회
		Delivery delivery = findDelivery(deliveryId);

		// 2. 해당 배송을 조회할 수 있는 사용자인지 확인
		confirmLoginUserRole(userId, delivery, role);

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
	public DeliveryUpdateResponse updateDelivery(UUID deliveryId, DeliveryUpdateRequest request, UUID userId,
												 String role) {
		// 1. 배송 조회
		Delivery delivery = findDelivery(deliveryId);

		// 2. 해당 배송을 수정할 수 있는 사용자인지 확인
		if (role.contains("COMPANY_MANAGER")) {
			log.info("업체 담당자는 수정 불가능");
			throw new DeliveryException("권한이 없습니다. ", HttpStatus.BAD_REQUEST);
		}

		confirmLoginUserRole(userId, delivery, role);

		// 3. 타 도메인으로 요청 후 검증이 필요한 수정 사항이 있는지 확인하기 - 수령인과 주소 변경 가능
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

			DeliveryShipperResponse responseShipper = shipperClient.assignShipper(
				request.getArriveHubId());

			if (responseShipper.getIsDeleted()) {
				throw new DeliveryException("배송 가능한 배송 담당자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			shipperId = responseShipper.getShipperId();

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
														   UUID userId, String role) {
		// 1. 배송 조회
		Delivery delivery = findDelivery(deliveryId);

		// 2. 해당 배송을 수정할 수 있는 사용자인지 확인
		if (role.contains("COMPANY_MANAGER")) {
			log.info("업체 담당자는 수정 불가능");
			throw new DeliveryException("권한이 없습니다. ", HttpStatus.BAD_REQUEST);
		}

		confirmLoginUserRole(userId, delivery, role);

		// 3. 배송 상태 수정하기
		Delivery updatingDelivery = request.toDelivery();
		delivery.updateDeliveryState(updatingDelivery);

		// 4. 변경된 상태에 따라서 order 측으로 요청 전송
		orderClient.notificationDeliveryState(DeliveryOrderStateUpdateRequest.builder()
				.orderId(delivery.getOrderId()).deliveryId(delivery.getDeliveryId())
				.deliveryState(delivery.getDeliveryState().toString()).build());

		// 5. 상태가 변경된 배송 정보 반환
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
	public void deleteDelivery(UUID deliveryId, UUID userId, String role) {
		// 1. 배송 검색
		Delivery delivery = findDelivery(deliveryId);

		// 2. 사용자 권한 확인
		if (role.contains("DELIVERY_MANAGER") || role.contains("COMPANY_MANAGER")) {
			throw new DeliveryException("삭제 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		if (role.contains("HUB_MANAGER")) {
			log.info("권한 확인 중 : " + role);
			UUID hubManagerHubId = hubClient.confirmHubFromUser(userId);
			if (! (hubManagerHubId.equals(delivery.getDepartHubId()) || hubManagerHubId.equals(delivery.getArriveHubId()))) {
				throw new DeliveryException("권한이 없습니다. ", HttpStatus.BAD_REQUEST);
			}
		}

		// 3. delivery 삭제 로직 추가 - delivery 상태에 따라서 배송이 시작되었다면 삭제가 불가능하도록 설정하기
		if ( (delivery.getDeliveryState().equals(DeliveryState.PENDING) ||
				delivery.getDeliveryState().equals(DeliveryState.PREPARING)) ) {
			throw new DeliveryException("배송을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 삭제 처리
		delivery.softDelete(userId);

		// 5. [orderClient] order 쪽으로도 요청 전송
		orderClient.notificationDeliveryState(DeliveryOrderStateUpdateRequest.builder()
				.orderId(delivery.getOrderId()).deliveryId(delivery.getDeliveryId())
				.deliveryState(DeliveryState.DELETED.toString()).build());
	}




	/**
	 * 허브 매니저, 배송 담당자 권한 확인
	 * 
	 * @param userId : 사용자 ID
	 * @param delivery : 배송 내역
	 * @param role : 역할
	 */
	private void confirmLoginUserRole(UUID userId, Delivery delivery, String role) {
		// HUB_MANAGER or DELIVERY_MANAGER인 경우만 확인하면 됨
		if (role.contains("HUB_MANAGER")) {
			log.info("권한 확인 중 : " + role);
			// 현재 로그인한 사용자가 허브 매니저인 경우,
			// Delivery의 arriveHubId or supplierHubId 와 일치하면 됨
			UUID hubManagerHubId = hubClient.confirmHubFromUser(userId);
			if (! (hubManagerHubId.equals(delivery.getDepartHubId()) || hubManagerHubId.equals(delivery.getArriveHubId()))) {
				throw new DeliveryException("권한이 없습니다. ", HttpStatus.BAD_REQUEST);
			}
		} else if (role.contains("DELIVERY_MANAGER")) {
			log.info("권한 확인 중 : " + role);
			if (!delivery.getShipperId().equals(userId)) {
				throw new DeliveryException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
			}
		}
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
