package com.i4u.order.application.service;

import java.util.UUID;

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
import com.i4u.order.presentation.client.ProductClient;
import com.i4u.order.presentation.dtos.request.OrderCompanyRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductRequest;
import com.i4u.order.presentation.dtos.request.OrderProductStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderProductUpdateResponse;
import com.i4u.order.presentation.dtos.request.OrderStatusUpdateByDeliveryRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;
import com.i4u.order.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;

import com.i4u.order.presentation.client.HubClient;

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

	/**
	 * 주문 생성
	 *
	 * @param request : 생성할 주문의 정보
	 * @param userId : 주문을 요청한 사용자
	 * @return : 생성한 주문 내용
	 */  // MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER (ALL) -> 권한 검증 과정 X
	public OrderCreateResponse createOrder(OrderCreateRequest request, String userId) {
		// 1. 권한 검증 필수 - 여기는 없음

		// 2. [companyClient] 업체 쪽으로 검증 요청 필요
		ResponseEntity<CommonResponse<OrderCompanyResponse>> responseCompany = companyClient.confirmCompany(OrderCompanyRequest.builder()
			.supplierId(request.getSupplierId()).recipientId(request.getRecipientId()).build());

		if (responseCompany.getBody().getData().getIsDeleted()) {
			// 업체가 둘 중 하나라도 없다면 Exception
			throw new OrderException("해당 업체가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. [productClient] 상품 쪽으로 검증 요청 필요 (상품의 개수랑 상품 ID를 같이 넘김)
		// 재고가 없거나 상품이 없다면 Exception
		ResponseEntity<CommonResponse<OrderProductResponse>> responseProduct = productClient.confirmProduct(OrderProductRequest.builder()
			.productId(request.getProductId()).productQuantity(request.getProductQuantity()).build());

		if (responseProduct.getBody().getData().getIsDeleted()) {
			throw new OrderException("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 주문 생성 (일단은 DeliveryId 없이 생성 후 저장) → 주문 상태는 PAID로 지정
		Long productTotalPrice = 100L;
		// Long productTotalPrice = responseProduct.getBody().getData().getProductTotalPrice();
		Order order = request.toOrder(productTotalPrice, responseCompany.getBody().getData().getSupplierHubId(), responseCompany.getBody().getData().getRecipientHubId(),
			UUID.fromString(userId));

		// 5. 생성한 주문 저장
		Order savedOrder = orderRepository.save(order);

		// 6. delivery 쪽으로 요청 전송 필요 (생성한 order의 정보와, 지금 주문을 요청한 사용자의 정보)
		OrderCompanyResponse company = responseCompany.getBody().getData();
		ResponseEntity<CommonResponse<OrderDeliveryResponse>> response = deliveryClient.createDelivery(OrderDeliveryRequest.builder()
				.orderId(savedOrder.getOrderId())
				.arriveHubId(company.getSupplierHubId())
				.departHubId(company.getRecipientHubId())
				.address(company.getAddress())
				// .recipientId(userId)
			.build());

		// 7. 받아온 내용으로 order Update
		// savedOrder.updateOrderStateFromDelivery(response.getDeliveryId(), switchIntoOrderStatus(response.getDeliveryState()));

		return OrderCreateResponse.fromOrder(savedOrder);
	}

	/**
	 * 주문 전체 조회 (+검색)
	 *
	 * @return : 조회한 주문 전체 내용
	 */ // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER(본인 주문)
	public PagedModel<OrderGetListResponse> getAllOrders(
		Pageable pageable, OrderSearchRequest request, String userId, String role) {
		// HUB MANAGER면 담당하는 허브가 필요하고, MASTER는 조건 X,
		// DELVIERY_MANAGER, COMPANY_MANAGER면 userID와 일치하는 경우만 조회 가능
		if (role.equals("ROLE_HUB_MANAGER")) {
			hubClient.getHubIdFromOrder(UUID.fromString(userId));
		}

		PagedModel<OrderGetListResponse> orderPage = orderRepository.searchOrder(pageable, request, UUID.fromString(userId), role);
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
	public OrderGetOneResponse getOneOrder(UUID orderId, String userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		//    허브 관리자라면 허브 담당자가 관리하는 허브의 주문만 조회 가능
		if (! ( role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 배송 담당자랑 업체 관리자는 본인이 주문한 ! 내역만 확인 가능
		if (role.equals("ROLE_DELIVERY_MANAGER") || role.equals("ROLE_COMPANY_MANAGER")) {
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
	public OrderUpdateResponse updateOrder(UUID orderId, OrderUpdateRequest request, String userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (! ( role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("ROLE_MASTER")) {
			throw new OrderException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
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
		ResponseEntity<CommonResponse<OrderProductUpdateResponse>> responseProduct = productClient.confirmProductUpdate(OrderProductUpdateRequest.builder()
			.beforeProductId(order.getProductId()).beforeProductQuantity(order.getProductQuantity())
			.afterProductId(request.getProductId()).afterProductQuantity(request.getProductQuantity()).build());

		if (responseProduct.getBody().getData().getIsDeleted()) {
			throw new OrderException("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		Long productTotalPrice = 100L;
		// Long productTotalPrice = responseProduct.getBody().getData().getProductTotalPrice();

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
	public OrderStatusUpdateResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request, String userId,
		String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (! ( role.equals("ROLE_HUB_MANAGER") &&
			confirmHubId(UUID.fromString(userId), order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("ROLE_MASTER")) {
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
			productClient.updateProductState(OrderProductStateUpdateRequest.builder()
				.productId(order.getProductId()).productQuantity(order.getProductQuantity()).build());

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
	public void deleteOrder(UUID orderId, String userId, String role) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		if (! ( role.equals("ROLE_HUB_MANAGER") &&
			  confirmHubId(UUID.fromString(userId), order.getRecipientHubId(), order.getSupplierHubId())) ) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!role.equals("ROLE_MASTER")) {
			throw new OrderException("수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		// 3. 삭제 진행
		order.softDelete(UUID.fromString(userId));
	}

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
	
}
