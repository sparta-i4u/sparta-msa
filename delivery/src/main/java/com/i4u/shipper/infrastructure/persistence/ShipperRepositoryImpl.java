package com.i4u.shipper.infrastructure.persistence;

import static com.i4u.shipper.domain.entity.QShipper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.domain.entity.QShipper;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;
import com.i4u.shipper.domain.repository.ShipperRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShipperRepositoryImpl implements ShipperRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ShipperListResponse> searchShippers(Pageable pageable, ShipperSearchRequest request,
		UUID userId, String role, UUID hubManagerHubId) {
		List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

		long pageSize = getPageSize(pageable.getPageSize(), pageable.getOffset());

		List<ShipperListResponse> results = queryFactory
			.select(Projections.fields(
				ShipperListResponse.class,
				QShipper.shipper.shipperId.as("shipperId"),
				shipper.hubId.as("hubId"),
				shipper.shipperType.as("shipperType"),
				shipper.shipperOrder.as("shipperOrder"),
				shipper.userId.as("userId")
			))
			.from(shipper)
			.where(
				confirmUserRole(userId, role, hubManagerHubId),
				confirmShipperType(request.getShipperType()),
				confirmHubId(request.getHubId()),
				confirmUserId(request.getUserId()),
				confirmShipperOrderBetween(request.getMinShipperOrder(), request.getMaxShipperOrder())
			)
			.orderBy(orders.toArray(new OrderSpecifier[0]))
			.offset(pageable.getOffset())
			.limit(pageSize)
			.fetch();

		// 전체 데이터 개수 계산
		long totalCount = results.isEmpty() ? 0 : countTotalCount(request, userId, role, hubManagerHubId);

		// 반환
		return new PageImpl<>(results, pageable, totalCount);
	}

	/**
	 * 배송 담당자 순서 지정
	 *
	 * @param hubId : 배송 담당자가 속한 허브 ID
	 * @return : 배송 담당자 순서
	 */
	@Override
	public Integer confirmShipperOrder(UUID hubId) {
		Tuple result = queryFactory
			.select(
				shipper.count(),
				shipper.shipperOrder.max()
			)
			.from(shipper)
			.where(
				shipper.hubId.eq(hubId)
			)
			.fetchOne();

		if (result == null) {
			return 1;
		}

		// count()는 Long 타입이므로 null 처리 후 기본값 설정
		Long shipperCount = Optional.ofNullable(result.get(shipper.count())).orElse(0L);
		Integer maxShipperOrder = result.get(shipper.shipperOrder.max());

		// shipper가 10명 이상이면 등록 불가능
		if (shipperCount >= 10) {
			return 0;
		}

		// 새 배송 순서는 max 값 + 1
		Integer newShipperOrder = maxShipperOrder != null ? maxShipperOrder + 1 : 1;
		return newShipperOrder;
	}

	@Override
	public Shipper assignShipper(UUID hubId) {
		System.out.println("hubId : " + hubId);
		Shipper assignShipper = queryFactory
			.selectFrom(shipper)
			.where(
				shipper.hubId.eq(hubId),
				shipper.shipperType.eq(ShipperType.COMPANY)
			)
			.orderBy(shipper.shipperOrder.asc())
			.limit(1)
			.fetchOne();

		return assignShipper;
	}

	@Override
	public Shipper assignNewShipper(UUID hubId, Integer shipperOrder) {
		Shipper assignShipper = queryFactory
			.selectFrom(shipper)
			.where(
				shipper.hubId.eq(hubId),
				shipper.shipperOrder.gt(shipperOrder),
				shipper.shipperType.eq(ShipperType.COMPANY)
			)
			.orderBy(shipper.shipperOrder.asc())
			.limit(1)
			.fetchOne();

		// shipperOrder보다 큰 값이 없을 경우, 가장 작은 shipperOrder 가진 Shipper 반환
		if (assignShipper == null) {
			assignShipper = assignShipper(hubId);
		}

		return assignShipper;
	}

	/**
	 * 전체 데이터 개수 계산
	 *
	 * @param request : 검색어 여부 확인
	 * @return : 전체 데이터 개수
	 */
	private Long countTotalCount(ShipperSearchRequest request, UUID userId, String role, UUID hubManagerHubId) {
		return queryFactory
			.select(shipper.count())
			.from(shipper)
			.where(
				confirmUserRole(userId, role, hubManagerHubId),
				confirmShipperType(request.getShipperType()),
				confirmHubId(request.getHubId()),
				confirmUserId(request.getUserId()),
				confirmShipperOrderBetween(request.getMinShipperOrder(), request.getMaxShipperOrder())
			)
			.fetchOne();
	}

	/* 검색어 */

	/**
	 * 배송 담당자 타입 검색하는 경우
	 *
	 * @param type : 배송 담당자 타입
	 * @return : 배송 담당자 타입과 동일 여부 반환
	 */
	private BooleanExpression confirmShipperType(ShipperType type) {
		return type != null ? shipper.shipperType.eq(type) : null;
	}

	/**
	 * 허브 ID로 검색하는 경우
	 *
	 * @param hubId : 허브 ID
	 * @return : 배송 담당자 타입과 동일 여부 반환
	 */
	private BooleanExpression confirmHubId(UUID hubId) {
		System.out.println("hubId : " + hubId);
		return hubId != null ? shipper.hubId.eq(hubId) : Expressions.asBoolean(true).isTrue();
	}

	/**
	 * 사용자 ID로 검색하는 경우
	 *
	 * @param userId : 사용자의 ID
	 * @return : 사용자 ID 동일 여부 반환
	 */
	private BooleanExpression confirmUserId(UUID userId) {
		return userId != null ? shipper.userId.eq(userId) : null;
	}

	/**
	 * 배송 담당자 순서의 최소, 최대값을 검색하는 경우
	 *
	 * @param minShipperOrder : 배송 담당자 순서 최솟값
	 * @param maxShipperOrder : 배송 담당자 순서 최댓값
	 * @return : 최소, 최대 범위 조건이 있는지 여부 반환
	 */
	private BooleanExpression confirmShipperOrderBetween(Integer minShipperOrder, Integer maxShipperOrder) {
		if (minShipperOrder == null && maxShipperOrder == null) {
			return null;
		}

		if (minShipperOrder != null && maxShipperOrder != null) {
			return shipper.shipperOrder.between(minShipperOrder, maxShipperOrder);
		} else if (minShipperOrder != null) {
			return shipper.shipperOrder.goe(minShipperOrder);
		} else {
			return shipper.shipperOrder.loe(maxShipperOrder);
		}
	}

	/**
	 * 사용자 권한에 따른 조건
	 *
	 * @param userId
	 * @param role            : 사용자의 권한
	 * @param hubManagerHubId
	 * @return : 권한에 따른 필터링 여부 반환
	 */
	private BooleanExpression confirmUserRole(UUID userId, String role, UUID hubManagerHubId) {
		if (role.equals("ROLE_MASTER")) {
			return null;
		} else if (role.equals("ROLE_HUB_MANAGER")) {
			return shipper.hubId.eq(hubManagerHubId);
		} else if (role.equals("ROLE_DELIVERY_MANAGER")) {
			return shipper.shipperId.eq(userId);
		}
		return null;
	}

	/**
	 * 페이지에 가져올 데이터의 개수
	 *
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

	/* 정렬 */

	/**
	 * 정렬 조건을 기반으로 OrderSpecifier 리스트 생성
	 *
	 * @param pageable : 정렬 조건을 포함한 Pageable 객체
	 * @return : QueryDSL에서 사용할 OrderSpecifier 리스트
	 */
	private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		/* 정렬 기준이 존재
		   pageable -> 클라이언트가 요청한 페이지 정보를 담고 있는 객체, 정렬 정보도 포함
		   sort -> 내부적으로 여러 개의 sort를 가짐 */
		if (pageable.getSort() != null) {
			log.info("정렬 조건 존재");
			// 정렬 정보 한 개씩 돌림
			for (Sort.Order sortOrder : pageable.getSort()) {
				orderSorting(sortOrder, orders);
			}
		}

		return orders;
	}

	/**
	 * Sort.Order기반의 정렬 조건을 정렬 리스트에 추가
	 *
	 * @param sortOrder : 정렬 정보(오름차순/내림차순)
	 * @param orders : 정렬 조건을 저장할 QueryDSL 리스트
	 */
	private void orderSorting(Sort.Order sortOrder, List<OrderSpecifier<?>> orders) {
		log.info("정렬 기준: " + sortOrder.getProperty() + " / " + "오름차순-내림차순: " + sortOrder.getDirection());
		com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

		// 정렬 기준에 따라
		switch (sortOrder.getProperty()) {
			case "createdAt" :
				log.info("정렬 조건: createdAt / 오름차순/내림차순" + direction);
				orders.add(new OrderSpecifier<>(direction, shipper.createdAt));
				break;
			case "modifiedAt" :
				log.info("정렬 조건: modifiedAt / 오름차순/내림차순" + direction);
				orders.add(new OrderSpecifier<>(direction, shipper.updatedAt));
				break;
			default :
				break;
		}
	}

}