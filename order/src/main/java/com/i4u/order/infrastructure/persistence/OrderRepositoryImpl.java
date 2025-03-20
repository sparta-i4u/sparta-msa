package com.i4u.order.infrastructure.persistence;

import static com.i4u.order.domain.entity.QOrder.*;

import com.i4u.order.domain.entity.OrderStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Repository;

import com.i4u.order.application.dtos.request.OrderSearchRequest;
import com.i4u.order.application.dtos.response.OrderGetListResponse;
import com.i4u.order.domain.repository.OrderRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public PagedModel<OrderGetListResponse> searchOrder(Pageable pageable, OrderSearchRequest request, UUID userId, String role) {
		List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

		long pageSize = getPageSize(pageable.getPageSize(), pageable.getOffset());

		List<OrderGetListResponse> results = queryFactory
				.select(Projections.fields(
					OrderGetListResponse.class,
					order.orderId.as("orderId"),
					order.supplierId.as("supplierId"),
					order.recipientId.as("recipientId"),
					order.productId.as("productId"),
					order.productQuantity.as("productQuantity"),
					order.requirement.as("requirement"),
					order.deliveryId.coalesce(Expressions.constant(UUID.fromString("00000000-0000-0000-0000-000000000000"))).as("deliveryId"),
					order.orderStatus.as("orderStatus")
				))
				.from(order)
				.where(
					// cofirmRole(role),
					supplierId(request.getSupplierId()),
					recipientId(request.getRecipientId()),
					productId(request.getProductId()),
					orderStatus(request.getOrderStatus())
				)
				.orderBy(orders.toArray(new OrderSpecifier[0]))
				.offset(pageable.getOffset())
				.limit(pageSize)
				.fetch();

		long totalCount = totalCount(request);

		Page<OrderGetListResponse> orderList = new PageImpl<>(results, pageable, totalCount);
		return new PagedModel<>(orderList);
	}

	private Long totalCount(OrderSearchRequest request) {
		return queryFactory
				.select(order.count())
				.from(order)
				.where(
						// cofirmRole(role),
						supplierId(request.getSupplierId()),
						recipientId(request.getRecipientId()),
						productId(request.getProductId()),
						orderStatus(request.getOrderStatus())
				)
				.fetchOne();
	}

	/**
	 * 페이지에 가져올 데이터의 개수
	 * @param pageSize : 페이지에 가져올 데이터 개수
	 * @param offset : 페이지 수
	 * @return : 0페이지면 page size, 1페이지부터는 10개 반환
	 */
	private long getPageSize(long pageSize, long offset) {
		// 10, 30, 50개 중 하나의 값
		if (offset == 0) {
			return pageSize;
		} else {
			return 10;
		}
	}

	/* 검색 조건 */

	/**
	 * 요청 업체를 검색하는 경우
	 * @param supplierId : 검색할 요청 업체의 ID
	 * @return : 요청 업체와 일치 여부 반환
	 */
	private BooleanExpression supplierId(UUID supplierId) {
		return supplierId != null ? order.supplierId.eq(supplierId) : null;
	}

	/**
	 * 수령 업체를 검색하는 경우
	 * @param recipientId : 검색할 수령 업체의 ID
	 * @return : 요청 업체와 일치 여부 반환
	 */
	private BooleanExpression recipientId(UUID recipientId) {
		return recipientId != null ? order.recipientId.eq(recipientId) : null;
	}

	/**
	 * 상품을 검색하는 경우
	 * @param productId : 검색할 상품의 ID
 	 * @return : 상품과 일치 여부 반환
	 */
	private BooleanExpression productId(UUID productId) {
		return productId != null ? order.productId.eq(productId) : null;
	}

	/**
	 * 주문 상태를 검색하는 경우
	 * @param orderStatus : 검색할 주문 상태
	 * @return : 주문 상태와 일치 여부 반환
	 */
	private BooleanExpression orderStatus(OrderStatus orderStatus) {
		return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
	}

//	private BooleanExpression confirmRole(String role) {
//		return null;
//	}

	/* 정렬 */

	/**
	 * 정렬 조건을 기반으로 OrderSpecifier 리스트 생성
	 * @param pageable : 정렬 조건을 포함한 Pageable 객체
	 * @return : QueryDSL에서 사용할 OrderSpecifier 리스트
	 */
	private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		/* 정렬 기준이 존재한다면
		   pageable -> 클라이언트가 요청한 페이지 정보를 담고 있는 객체, 정렬 정보도 포함
		   sort -> 내부적으로 여러 개의 sort를 가짐 */
		if (pageable.getSort() != null) {
			log.info("정렬 조건이 있어요");
			// 정렬 정보 한 개씩 돌림
			for (Sort.Order sortOrder : pageable.getSort()) {
				orderSorting(sortOrder, orders);
			}
		}

		return orders;
	}

	/**
	 * Sort.Order기반의 정렬 조건을 정렬 리스트에 추가
	 * @param sortOrder : 정렬 정보(오름차순/내림차순)
	 * @param orders : 정렬 조건을 저장할 QueryDSL 리스트
	 */
	private void orderSorting(Sort.Order sortOrder, List<OrderSpecifier<?>> orders) {
		log.info("정렬 기준: " + sortOrder.getProperty() + " / " + "오름차순-내림차순: " + sortOrder.getDirection());
		com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

		// 정렬 기준의 경우에 따라
		switch (sortOrder.getProperty()) {
			case "createdAt" :
				log.info("정렬 조건: createdAt / 오름차순/내림차순" + direction);
				 orders.add(new OrderSpecifier<>(direction, order.createdAt));
				break;
			case "modifiedAt" :
				log.info("정렬 조건: modifiedAt / 오름차순/내림차순" + direction);
				 orders.add(new OrderSpecifier<>(direction, order.updatedAt));
				break;
			default :
				break;
		}
	}
}
