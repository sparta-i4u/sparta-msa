package com.i4u.order.application.service;

import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.dtos.request.OrderSearchRequest;
import com.i4u.order.application.dtos.request.OrderStatusUpdateRequest;
import com.i4u.order.application.dtos.request.OrderUpdateRequest;
import com.i4u.order.application.dtos.response.*;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.presentation.exception.OrderException;
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

	// TODO : 각 로직마다 권한 확인하는 로직 추가 필수

	/**
	 * 주문 생성
	 *
	 * @param request : 생성할 주문의 정보
	 * @return : 생성한 주문 내용
	 */
	public OrderCreateResponse createOrder(OrderCreateRequest request /*, 주문한 현재 사용자 */) {
		// TODO : [company] 업체 쪽으로 검증 요청 필요
		// 생성/수령 업체 둘 다 존재해야 함
		// 하나라도 없으면 Exception
		
		// TODO : [product] 상품 쪽으로 검증 요청 필요 (상품의 개수랑 상품 ID를 같이 넘김)
		// 재고가 없으면 exception


		// 일단 주문 생성 하고, delivery ID 없이 생성 후 저장
		Order order = request.toOrder();

		orderRepository.save(order);

		// TODO : delivery 쪽으로 요청 전송 필요 (생성한 order의 정보와, 지금 주문을 요청한 사용자의 정보)
		// delivery 가서 생성 후 받아온 deliveryID를 저장해야 함
		// 그걸 저장하면서 orderStatus 변경하기
		return OrderCreateResponse.toDto(order);
	}

	/**
	 * 주문 전체 조회 (+검색)
	 *
	 * @return : 조회한 주문 전체 내용
	 */
	public List<OrderGetListResponse> getAllOrders(Pageable pageable, OrderSearchRequest request) {
		PagedModel<OrderGetListResponse> orderPage = orderRepository.searchOrder(pageable, request);

		List<Order> orders = orderRepository.findAll();
		return orders.stream()
			.map(OrderGetListResponse::toDto)
			.collect(Collectors.toList());
	}

	/**
	 * 주문 단건 조회
	 * 
	 * @param orderId : 조회할 주문의 ID
	 * @return : 조회된 주문의 내용
	 */
	public OrderGetOneResponse getOneOrder(UUID orderId) {
		Order order = findOrder(orderId);

		return OrderGetOneResponse.toDto(order);
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
		Order order = findOrder(orderId);

		// 주문 상태를 수정할 수 있는 사용자인지 확인
		// 아니면 Exception

		// TODO : 현재 주문의 상태가 결제 완료인 경우만 수정 가능하도록 설정하기(배송 ID가 배정되어버리면 변경 불가능)

		// 주문 내용 수정
		Order updateOrder = request.toOrder();
		order.updateOrder(updateOrder);

		return OrderUpdateResponse.toDto(order);
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
		Order order = findOrder(orderId);

		// 주문 상태를 수정할 수 있는 사용자인지 확인
		// 아니면 Exception
		
		// 주문 상태 수정
		Order updateOrder = request.toOrder();
		order.updateOrderState(updateOrder);
		
		return OrderStatusUpdateResponse.toDto(order);
	}

	/**
	 * 주문 삭제
	 * 
	 * @param orderId : 삭제할 주문의 ID
	 */
	@Transactional
	public void deleteOrder(UUID orderId) {
		Order order = findOrder(orderId);

		// 삭제할 권한이 있는 사용자인지 확인 후 삭제 작업 진행
		// 아니면 Exception
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
