package com.i4u.delivery.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.i4u.client.AuthClient;
import com.i4u.client.HubClient;
import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.exception.DeliveryException;
import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.client.MessageClient;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;
import com.i4u.shipper.application.service.ShipperClient;
import com.i4u.shipper.application.service.ShipperClientService;
import com.i4u.shipper.presentation.dtos.request.MessageRequest;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryClientService {

	private final DeliveryRepository deliveryRepository;
	private final HubClient hubClient;
	private final AuthClient authClient;
	private final ShipperClient shipperClient;
	private final MessageClient messageClient;

	private final RabbitTemplate rabbitTemplate;

	@Value("${i4u.err.queue.order}")
	private String orderErrorQueue;

	public Map<String, Object> createDelivery(DeliveryCreateRequest request) {
		try {
			// 1. [hubClient] 허브 검증 (출발 허브, 도착 허브 검증)
			System.out.println("supplier: " + request.getSupplierHubId());
			System.out.println("recipient: " + request.getRecipientHubId());
			DeliveryHubCreateResponse responseHub = hubClient.confirmHubsFromDelivery(
				request.getSupplierHubId(), request.getRecipientHubId() );

			if (responseHub.getIsDeleted()) {
				throw new DeliveryException("배송할 수 있는 허브가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 2. [userClient] 수령인 슬랙 ID 받아오기
			System.out.println("수령인 ID : " + request.getRecipientId());
			ConfirmUserResponse responseUser = authClient.confirmUser(request.getRecipientId());

			if (responseUser.getIsDeleted()) {
				throw new DeliveryException("수령인이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
			}

			// 3. [shipperClient]  배송 담당자 배정 요청
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

			Map<String, Object> response = new HashMap<>();
			response.put("orderId", savedDelivery.getOrderId());
			response.put("deliveryState", savedDelivery.getDeliveryState());
			response.put("deliveryId", savedDelivery.getDeliveryId());

			// 6. 메세지 전송 요청 보내기
			// sendMessage(delivery, responseShipper, responseUser, request);

			return response;
		} catch (Exception e) {
			Map<String, Object> errorMessage = new HashMap<>();
			errorMessage.put("orderId", request.getOrderId());
			errorMessage.put("errorMessage", e.getMessage());
			errorMessage.put("errorCode", "DELIVERY_ERROR");

			rabbitTemplate.convertAndSend(orderErrorQueue, errorMessage);

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
	 * 주문 수정에 따른 배송 수정
	 * 
	 * @param request : 수정 내용
	 */
	@Transactional
	public void updateDeliveryByOrder(OrderDeliveryUpdateRequest request) {
		// 1. 주문 ID에 해당하는 배송 검색
		Delivery delivery = deliveryRepository.findByOrderId(request.getOrderId())
			.orElseThrow(() -> new IllegalArgumentException("해당 배송은 없습니다."));

		// 2. [hubClient] 검증 필요
		if (request.getSupplierHubId().equals(delivery.getDepartHubId())) {
			// hub가 같으니까 바꿀 필요가 없음
			throw new IllegalArgumentException("허브가 변경되지 않았습니다. ");
		}
		
		// 3. [shipperClient] 재배정 필요
		// shipperClient 호출해서 재배정 받고, shipperId 변경하기
		DeliveryShipperResponse response = shipperClient.assignShipper(
			request.getSupplierHubId()
		);

		delivery.updateDeliveryShipperByOrder(response.getShipperId());

		// 4. 배송 내역이 수정되었으므로 배송 상태도 수정 (출고 준비 중으로 변경)
		delivery.updateDeliveryStateByOrder(DeliveryState.PREPARING);
	}

	/**
	 * 주문 취소에 의한 배송 취소
	 * 
	 * @param orderCanceled : 주문이 취소된 주문 내역
	 */
	@Transactional
	public void updateDeliveryStateByOrder(OrderDeliveryStateUpdateRequest orderCanceled) {
		// 주문이 취소된 경우
		// 1. 받아온 배송 ID로 배송 검색
		Delivery delivery = deliveryRepository.findById(orderCanceled.getDeliveryId())
			.filter(o -> o.getOrderId().equals(orderCanceled.getOrderId()))
			.orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

		// 2. 배송 상태 업데이트
		delivery.updateDeliveryStateByOrder(DeliveryState.ORDER_CANCELED);
	}
}
