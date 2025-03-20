package com.i4u.order.application.service;

import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.dtos.request.OrderStatusUpdateRequest;
import com.i4u.order.application.dtos.request.OrderUpdateRequest;
import com.i4u.order.application.dtos.response.*;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.application.exception.OrderException;
import com.i4u.order.presentation.client.CompanyClient;
import com.i4u.order.presentation.client.DeliveryClient;
import com.i4u.order.presentation.client.ProductClient;
import com.i4u.order.presentation.dtos.request.OrderCompanyRequest;
import com.i4u.order.presentation.dtos.request.OrderCompanyUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductRequest;
import com.i4u.order.presentation.dtos.request.OrderProductStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderStatusUpdateByDeliveryRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;
import com.i4u.order.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;
import com.i4u.order.presentation.dtos.response.OrderProductUpdateResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final CompanyClient companyClient;
	private final ProductClient productClient;
	private final DeliveryClient deliveryClient;

	// TODO : 각 로직마다 권한 확인하는 로직 추가 필수

	/**
	 * 주문 생성
	 *
	 * @param request : 생성할 주문의 정보
	 * @return : 생성한 주문 내용
	 */
	public OrderCreateResponse createOrder(OrderCreateRequest request /*, UUID userId, String Role */) {
		// 1. 권한 검증 필수
		// {권한을 보고 주문 생성 권한이 있는지 확인하기}

		// 2. [companyClient] 업체 쪽으로 검증 요청 필요
		// OrderCompanyResponse responseCompany = companyClient.confirmCompany(OrderCompanyRequest.builder()
		// 	.supplierId(request.getSupplierId()).recipientId(request.getRecipientId()).build());
		//
		// if (responseCompany.getIsDeleted()) {
		// 	// 업체가 둘 중 하나라도 없다면 Exception
		// 	throw new OrderException("해당 업체가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }

		// 3. [productClient] 상품 쪽으로 검증 요청 필요 (상품의 개수랑 상품 ID를 같이 넘김)
		// 재고가 없거나 상품이 없다면 Exception
		// OrderProductResponse responseProduct = productClient.confirmProduct(OrderProductRequest.builder()
		// 	.productId(request.getProductId()).productQuantity(request.getProductQuantity()).build());
		//
		// if (responseProduct.getIsDeleted()) {
		// 	throw new OrderException("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }

		// 4. 주문 생성 (일단은 DeliveryId 없이 생성 후 저장) → 주문 상태는 PAID로 지정
		Long productTotalPrice = 100L;
		// Long productTotalPrice = responseProduct.getProductTotalPrice();
		Order order = request.toOrder(productTotalPrice);

		// 5. 생성한 주문 저장
		Order savedOrder = orderRepository.save(order);

		// 6. delivery 쪽으로 요청 전송 필요 (생성한 order의 정보와, 지금 주문을 요청한 사용자의 정보)
		// OrderDeliveryResponse response = deliveryClient.createDelivery(OrderDeliveryRequest.builder()
		// 		.orderId(savedOrder.getOrderId())
		// 		.supplierHubId(responseCompany.getSupplierHubId())
		// 		.recipientHubId(responseCompany.getRecipientHubId())
		// 		.address(responseCompany.getAddress())
		// 		// .userId(userId)
		// 	.build());

		// 7. 받아온 내용으로 order Update
		// savedOrder.updateOrderStateFromDelivery(response.getDeliveryId(), switchIntoOrderStatus(response.getDeliveryState()));

		return OrderCreateResponse.fromOrder(savedOrder);
	}

	/**
	 * 주문 전체 조회 (+검색)
	 *
	 * @return : 조회한 주문 전체 내용
	 */
	public PagedModel<OrderGetListResponse> getAllOrders(Pageable pageable, OrderSearchRequest request) {
		PagedModel<OrderGetListResponse> orderPage = orderRepository.searchOrder(pageable, request);
		return orderPage;
	}

	/**
	 * 주문 단건 조회
	 * 
	 * @param orderId : 조회할 주문의 ID
	 * @return : 조회된 주문의 내용
	 */
	public OrderGetOneResponse getOneOrder(UUID orderId) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		// {권한을 보고 주문 조회 권한이 있는지 확인하기 - 없으면 Exception}

		return OrderGetOneResponse.fromOrder(order);
	}

	/**
	 * 주문 수정
	 * 
	 * @param orderId : 수정할 주문의 ID
	 * @param request : 수정할 주문 내용
	 * @return : 수정된 주문 정보
	 */
	@Transactional
	public OrderUpdateResponse updateOrder(UUID orderId, OrderUpdateRequest request) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		// {권한을 보고 주문 조회 권한이 있는지 확인하기 - 없으면 Exception}

		// 3. 현재 주문의 상태가 결제 완료인 경우만 수정 가능하도록 설정하기 (배송 ID가 배정되어버리면 변경 불가능)
		if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
			throw new OrderException("주문 내역을 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 주문 내용 수정

		// 4-1. [companyClient] 수령 업체 검증
		// OrderCompanyUpdateResponse responseCompany = companyClient.confirmCompanyUpdate(OrderCompanyUpdateRequest.builder()
		// 	.supplierId(request.getSupplierId()).build());
		//
		// if (responseCompany.getIsDeleted()) {
		// 	throw new OrderException("해당 업체가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }

		// 4-2. [productClient] 상품 검증
		// OrderProductUpdateResponse responseProduct = productClient.confirmProductUpdate(OrderProductUpdateRequest.builder()
		// 	.beforeProductId(order.getProductId()).beforeProductQuantity(order.getProductQuantity())
		// 	.afterProductId(request.getProductId()).afterProductQuantity(request.getProductQuantity()).build());
		//
		// if (responseProduct.getIsDeleted()) {
		// 	throw new OrderException("해당 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		// }

		Long productTotalPrice = 100L;
		// Long productTotalPrice = responseProduct.getProductTotalPrice();

		// 5. 주문 상태 수정
		Order updateOrder = request.toOrder(productTotalPrice);
		order.updateOrder(updateOrder);

		// 6. [deliveryClient] 변경 사항에 대한 배송 수정 요청 전송 (공급 업체가 바뀌어서 허브도 바뀜)
		// deliveryClient.updateDelivery(OrderDeliveryUpdateRequest.builder()
		// 	.orderId(updateOrder.getOrderId()).supplierHubId(responseCompany.getSupplierHubId()).build());

		return OrderUpdateResponse.fromOrder(order);
	}

	/**
	 * 주문 상태 수정
	 * 
	 * @param orderId : 상태를 변경할 주문의 ID
	 * @param request : 변경할 상태 정보
	 * @return : 변경된 주문 정보
	 */
	@Transactional
	public OrderStatusUpdateResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		// {권한을 보고 주문 조회 권한이 있는지 확인하기 - 없으면 Exception}
		
		// 3. 주문 상태 수정
		Order updateOrder = request.toOrder();
		order.updateOrderState(updateOrder);

		// 4. 주문을 취소했다면 delivery, product 측으로 요청 전송 필요
		// if (updateOrder.getOrderStatus().equals(OrderStatus.ORDER_CANCELED)) {
		// 	// [deliveryClient] 주문이 취소되었으므로 배송 update 필요 (deliveryId가 null 이면 ..?)
		// 	deliveryClient.updateDeliveryState(OrderDeliveryStateUpdateRequest.builder()
		// 		.orderId(order.getOrderId()).orderState("ORDER_CANCELED").deliveryId(order.getDeliveryId()).build());
		//
		// 	// [productClient] 주문이 취소되었으므로 재고 update 필요
		// 	productClient.updateProductState(OrderProductStateUpdateRequest.builder()
		// 		.productId(order.getProductId()).productQuantity(order.getProductQuantity()).build());
		// }

		return OrderStatusUpdateResponse.fromOrder(order);
	}

	/**
	 * Delivery 측에서 요청을 받아 수정할 주문 상태
	 *
	 * @param orderId : 상태를 변경할 주문 ID
	 * @param request : 변경할 상태 정보
	 * @return : 변경된 주문 정보
	 */
	@Transactional
	public OrderStatusUpdateResponse updateOrderStatusWithDelivery(UUID orderId, OrderStatusUpdateByDeliveryRequest request) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		// {권한을 보고 주문 조회 권한이 있는지 확인하기 - 없으면 Exception}

		// 3. 주문 상태 수정 (이건 Delivery 측에서 요청을 받고 변환할 내용 - 다시 delivery 측으로 요청을 전송해줄 필요 X)
		Order updateOrder = request.toOrder(switchIntoOrderStatus(request.getDeliveryState()));
		order.updateOrderState(updateOrder);

		return OrderStatusUpdateResponse.fromOrder(order);
	}

	/**
	 * 주문 삭제
	 * 
	 * @param orderId : 삭제할 주문의 ID
	 */
	@Transactional
	public void deleteOrder(UUID orderId) {
		// 1. orderId에 해당하는 Order 검색
		Order order = findOrder(orderId);

		// 2. 권한 검증 필수
		// {권한을 보고 주문 조회 권한이 있는지 확인하기 - 없으면 Exception}

		// 3. 삭제 진행
		// order.softDelete(userId);
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
