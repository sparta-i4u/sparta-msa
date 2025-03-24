package com.i4u.order.application.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.dtos.request.OrderSearchRequest;
import com.i4u.order.application.dtos.request.OrderStatusUpdateRequest;
import com.i4u.order.application.dtos.request.OrderUpdateRequest;
import com.i4u.order.application.dtos.response.OrderCreateResponse;
import com.i4u.order.application.dtos.response.OrderGetListResponse;
import com.i4u.order.application.dtos.response.OrderGetOneResponse;
import com.i4u.order.application.dtos.response.OrderStatusUpdateResponse;
import com.i4u.order.application.dtos.response.OrderUpdateResponse;
import com.i4u.order.application.exception.OrderException;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.presentation.client.CompanyClient;
import com.i4u.order.presentation.client.DeliveryClient;
import com.i4u.order.presentation.client.HubClient;
import com.i4u.order.presentation.client.ProductClient;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderStatusUpdateByDeliveryRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final CompanyClient companyClient;
	private final ProductClient productClient;
	private final DeliveryClient deliveryClient;
	private final HubClient hubClient;

	private final RabbitTemplate rabbitTemplate;

	@Value("${i4u.queue.delivery}")
	private String deliveryQueue;

	/**
	 * 주문 생성
	 *
	 * @param request : 생성할 주문의 정보
	 * @param userId : 주문을 요청한 사용자
	 * @return : 생성한 주문 내용
	 */  // MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER (ALL) -> 권한 검증 과정 X
	@Transactional
	public OrderCreateResponse createOrder(OrderCreateRequest request, UUID userId) {
		// 1. [companyClient] 업체 쪽으로 검증 요청 필요
		OrderCompanyResponse responseCompany = companyClient.confirmCompany(
			request.getSupplierId(), request.getRecipientId());

		if (responseCompany.getIsDeleted()) {
			// 업체가 둘 중 하나라도 없다면 Exception
			throw new OrderException("해당 업체가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. [productClient] 상품 쪽으로 검증 요청 필요 (상품의 개수랑 상품 ID를 같이 넘김)
		// 재고가 없거나 상품이 없다면 Exception
		OrderProductResponse responseProduct = productClient.confirmProduct(
				request.getProductId(), request.getProductQuantity());

		if (responseProduct.getIsDeleted()) {
			throw new OrderException("해당 상품이 존재하지 않거나 재고가 충분하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 주문 생성 (일단은 DeliveryId 없이 생성 후 저장) → 주문 상태는 PAID로 지정
		Long productTotalPrice = responseProduct.getProductTotalPrice();
		Order order = request.toOrder(productTotalPrice, responseCompany.getSupplierHubId(), responseCompany.getRecipientHubId(),
				userId);

		// 4. 생성한 주문 저장
		Order savedOrder = orderRepository.save(order);

		// 5. delivery 쪽으로 요청 전송 필요 (생성한 order의 정보와, 지금 주문을 요청한 사용자의 정보)
		OrderDeliveryRequest message = OrderDeliveryRequest.builder()
				.orderId(savedOrder.getOrderId())
				.recipientHubId(responseCompany.getRecipientHubId())
				.supplierHubId(responseCompany.getSupplierHubId())
				.address(responseCompany.getAddress())
				.requirement(savedOrder.getRequirement())
				.recipientId(userId)
				.productId(savedOrder.getProductId())
				.productName(responseProduct.getProductName())
				.productQuantity(savedOrder.getProductQuantity())
				.build();

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> responseMap = (Map<String, Object>) rabbitTemplate.convertSendAndReceive(deliveryQueue, message);

			if (isDeliveryFailed(responseMap)) {
				handleDeliveryFailure(savedOrder);
			} else {
				processDeliverySuccess(savedOrder, responseMap);
			}
		} catch (Exception e) {
			handleDeliveryFailure(savedOrder);
		}

		return OrderCreateResponse.fromOrder(savedOrder);
	}
	
	/**
	 * 주문 전체 조회 (+검색)
	 *
	 * @return : 조회한 주문 전체 내용
	 */ // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER(본인 주문)
	public PagedModel<OrderGetListResponse> getAllOrders(
			Pageable pageable, OrderSearchRequest request, UUID userId, String role) {
		// HUB MANAGER면 담당하는 허브가 필요하고, MASTER는 조건 X,
		// DELVIERY_MANAGER, COMPANY_MANAGER면 userID와 일치하는 경우만 조회 가능\
		UUID hubManagerHubId = null;
		if (role.equals("HUB_MANAGER")) {
			hubManagerHubId = hubClient.getHubIdFromOrder(userId);
		}

		PagedModel<OrderGetListResponse> orderPage = orderRepository.searchOrder(pageable, request, userId, role, hubManagerHubId);
		return orderPage;
	}

	/**
	 * 주문 단건 조회
	 *
	 * @param orderId : 조회할 주문의 ID
	 * @param userId
	 * @param role
	 * @return : 조회된 주문의 내용
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER(본인 주문)
	public OrderGetOneResponse getOneOrder(UUID orderId, UUID userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		//    허브 관리자라면 허브 담당자가 관리하는 허브의 주문만 조회 가능
		if (  role.equals("HUB_MANAGER") &&
				!confirmHubId(userId, order.getRecipientHubId(), order.getSupplierHubId()) ) {
			throw new OrderException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 배송 담당자랑 업체 관리자는 본인이 주문한 ! 내역만 확인 가능
		if (role.equals("DELIVERY") || role.equals("COMPANY_MANAGER")) {
			if (!order.getUserId().equals(userId)) {
				throw new OrderException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
			}
		}

		return OrderGetOneResponse.fromOrder(order);
	}

	/**
	 * 주문 수정
	 *
	 * @param orderId : 수정할 주문의 ID
	 * @param request : 수정할 주문 내용
	 * @param userId
	 * @param role
	 * @return : 수정된 주문 정보
	 */ // MASTER, HUB_MANAGER(담당 허브)
	@Transactional
	public OrderUpdateResponse updateOrder(UUID orderId, OrderUpdateRequest request, UUID userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (! ( role.equals("HUB_MANAGER") &&
				confirmHubId(userId, order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("ROLE_MASTER")) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 현재 주문의 상태가 결제 완료인 경우만 수정 가능하도록 설정하기 (배송 ID가 배정되어버리면 변경 불가능)
		if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
			throw new OrderException("주문 내역을 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 주문 내용 수정

		// 4-1. [companyClient] 수령 업체 검증
		ResponseEntity<CommonResponse<OrderCompanyUpdateResponse>> responseCompany = companyClient.confirmCompanyUpdate(request.getSupplierId());

		if (responseCompany.getBody().getData().getIsDeleted()) {
			throw new OrderException("해당 업체가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4-2. [productClient] 상품 검증
		Map<String, Object> responseProduct = productClient.confirmProductUpdate(order.getProductId(), order.getProductQuantity(),
				request.getProductId(), request.getProductQuantity());

		if ((Boolean) responseProduct.get("isDeleted")) {
			throw new OrderException("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		Long productTotalPrice = (Long) responseProduct.get("productTotalPrice");

		// 5. 주문 상태 수정
		Order updateOrder = request.toOrder(productTotalPrice, responseCompany.getBody().getData().getSupplierHubId());
		order.updateOrder(updateOrder);

		// 6. [deliveryClient] 변경 사항에 대한 배송 수정 요청 전송 (공급 업체가 바뀌어서 허브도 바뀜)
		deliveryClient.updateDeliveryByOrder(OrderDeliveryUpdateRequest.builder()
				.orderId(updateOrder.getOrderId()).supplierHubId(responseCompany.getBody().getData().getSupplierHubId()).build());

		return OrderUpdateResponse.fromOrder(order);
	}

	/**
	 * 주문 상태 수정
	 *
	 * @param orderId : 상태를 변경할 주문의 ID
	 * @param request : 변경할 상태 정보
	 * @param userId
	 * @param role
	 * @return : 변경된 주문 정보
	 */ // MASTER, HUB_MANAGER(담당 허브)
	@Transactional
	public OrderStatusUpdateResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request, UUID userId,
													   String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (! ( role.equals("HUB_MANAGER") &&
				confirmHubId(userId, order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("MASTER") || !role.equals("HUB_MANAGER")) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 주문 상태 수정
		Order updateOrder = request.toOrder();
		order.updateOrderState(updateOrder);

		// 4. 주문을 취소했다면 delivery, product 측으로 요청 전송 필요
		if (updateOrder.getOrderStatus().equals(OrderStatus.ORDER_CANCELED) &&
				order.getDeliveryId() != null) {

			// 4-1. [deliveryClient] 주문이 취소되었으므로 배송 update 필요
			deliveryClient.updateDeliveryStateByOrder(OrderDeliveryStateUpdateRequest.builder()
					.orderId(order.getOrderId()).deliveryId(order.getDeliveryId()).build());

			// 4-2. [productClient] 주문이 취소되었으므로 재고 update 필요
			productClient.updateProductState(order.getProductId(), order.getProductQuantity());

		}

		return OrderStatusUpdateResponse.fromOrder(order);
	}

	/**
	 * Delivery 측에서 요청을 받아 수정할 주문 상태 (검증 X)
	 *
	 * @param orderId : 상태를 변경할 주문 ID
	 * @param request : 변경할 상태 정보
	 * @return : 변경된 주문 정보
	 */
	@Transactional
	public void updateOrderStatusByDelivery(UUID orderId, OrderStatusUpdateByDeliveryRequest request) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 주문 상태 수정 (이건 Delivery 측에서 요청을 받고 변환할 내용 - 다시 delivery 측으로 요청을 전송해줄 필요 X)
		Order updateOrder = request.toOrder(switchIntoOrderStatus(request.getDeliveryState()));
		order.updateOrderState(updateOrder);
	}

	/**
	 * 주문 삭제
	 *
	 * @param orderId : 삭제할 주문의 ID
	 * @param userId
	 * @param role
	 */  // MASTER, HUB_MANAGER(담당 허브)
	@Transactional
	public void deleteOrder(UUID orderId, UUID userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (( role.equals("HUB_MANAGER") &&
				! confirmHubId(userId, order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("MASTER")) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 삭제 진행
		order.softDelete(userId);
	}

	/**
	 * 허브 담당자의 허브 ID를 확인하는 메서드
	 *
	 * @param userId : 허브 담당자의 ID
	 * @param realHubId1 : 허브 ID 1
	 * @param realHubId2 : 허브 ID 2
	 * @return : 일치 여부 반환
	 */
	private Boolean confirmHubId(UUID userId, UUID realHubId1, UUID realHubId2) {
		UUID hubId = hubClient.getHubIdFromOrder(userId);
		if (hubId == null || ! hubId.equals(realHubId1) || ! hubId.equals(realHubId2)) {
			return false;
		}
		return true;
	}

	/**
	 * 배송 상태에 따른 주문 상태 수정
	 *
	 * @param deliveryStatus : 배송 상태
	 * @return : 올바른 주문 상태로 반환
	 */
	private OrderStatus switchIntoOrderStatus(String deliveryStatus) {
		switch (deliveryStatus) {
			case "SHIPPED" :
				return OrderStatus.SHIPPED;
			case "OUT_FOR_DELIVERY" :
				return OrderStatus.OUT_FOR_DELIVERY;
			case "DELIVERED" :
				return OrderStatus.DELIVERED;
			case "DELIVERY_CANCELED" :
				return OrderStatus.DELIVERY_CANCELED;
			default :
				return OrderStatus.SCHEDULED;
		}
	}

	/**
	 * 주문 검색
	 * @param orderId : 검색할 주문의 ID
	 * @return : 검색한 주문 내용
	 */
	private Order findOrder(UUID orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException("해당 주문을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
	}

	/**
	 * 배송 실패 여부 판단
	 * 
	 * @param responseMap : 응답 내용
	 * @return : 배송 실패 여부
	 */
	private boolean isDeliveryFailed(Map<String, Object> responseMap) {
		return responseMap == null || Optional.ofNullable(responseMap.get("deliveryState"))
			.map(Object::toString)
			.orElse("")
			.contains("ERROR");
	}

	/**
	 * 배송 실패 처리
	 * 
	 * @param savedOrder : 저장된 주문
	 */
	private void handleDeliveryFailure(Order savedOrder) {
		log.error("배송 실패 처리: 주문 상태를 '배송 실패'로 변경");
		savedOrder.updateOrderStateByDeliveryError(OrderStatus.DELIVERY_FAILED);

		// 제품 상태 업데이트 재요청
		try {
			productClient.updateProductState(savedOrder.getProductId(), savedOrder.getProductQuantity());
			log.error("상품 서비스에 재요청 보냄");
		} catch (Exception e) {
			log.error("상품 서비스 요청 중 예외 발생: " + e.getMessage());
		}
	}

	/**
	 * 배송 성공 처리
	 * 
	 * @param savedOrder : 저장된 주문 내용
	 * @param responseMap : 응답 내용
	 */
	private void processDeliverySuccess(Order savedOrder, Map<String, Object> responseMap) {
		log.info("배송 성공: 주문 상태를 '배송 예정'으로 변경");

		UUID deliveryId = UUID.fromString(responseMap.get("deliveryId").toString());
		savedOrder.updateOrderStateFromDelivery(deliveryId, OrderStatus.SCHEDULED);
	}

	/**
	 * 주문 상태 배송 실패로 수정
	 * 
	 * @param errorMessage : 오류 메시지 내용
	 */
	@Transactional
	public void rollbackOrder(Map<String, Object> errorMessage) {
		log.error("배송 오류 메시지 수신: " + errorMessage);

		UUID orderId = (UUID) errorMessage.get("orderId");
		String errorDetails = (String) errorMessage.get("errorMessage");

		Order order = findOrder(orderId);
		order.updateOrderStateByDeliveryError(OrderStatus.DELIVERY_FAILED);

		// 상품 재고 증가 요청 전송
		productClient.updateProductState(
			order.getProductId(), order.getProductQuantity());
	}

}