package com.i4u.delivery.infrastructure.persistence;

import static com.i4u.delivery.domain.entity.QDelivery.*;

import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PagedModel<DeliveryGetListResponse> searchDeliveries(Pageable pageable, DeliverySearchRequest request,
		UUID userId, String role, UUID hubManagerHubId) {
        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);
        long pageSize = getPageSize(pageable.getPageSize(), pageable.getOffset());

        List<DeliveryGetListResponse> results = jpaQueryFactory
            .select(Projections.fields(
                DeliveryGetListResponse.class,
                delivery.deliveryId.as("deliveryId"),
                delivery.orderId.as("orderId"),
                delivery.deliveryState.as("deliveryState"),
                delivery.departHubId.as("departHubId"),
                delivery.arriveHubId.as("arriveHubId"),
                delivery.address.as("address"),
                delivery.recipientId.as("recipientId"),
                delivery.recipientSlackId.as("recipientSlackId"),
                delivery.shipperId.as("shipperId")
            ))
            .from(delivery)
            .where(
                confirmRole(role, userId, hubManagerHubId),
                orderId(request.getOrderId()),
                deliveryState(request.getDeliveryState()),
                shipperId(request.getShipperId()),
                recipientId(request.getRecipientId()),
                recipientSlackId(request.getRecipientSlackId())
            )
            .orderBy(orders.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageSize)
            .fetch();

        long totalCount = getTotalCount(request);
        Page<DeliveryGetListResponse> deliveryList = new PageImpl<>(results, pageable, totalCount);
        return new PagedModel<>(deliveryList);
    }

    /**
     * 전체 데이터 개수 반환
     * @param request : 검색어 조건
     * @return : 검색어 조건에 해당하는 데이터 개수 반환
     */
    private Long getTotalCount(DeliverySearchRequest request) {
        // return ~
        return jpaQueryFactory.select(delivery.count())
            .from(delivery)
            .where(
                // confirmRole(role),
                orderId(request.getOrderId()),
                deliveryState(request.getDeliveryState()),
                shipperId(request.getShipperId()),
                recipientId(request.getRecipientId()),
                recipientSlackId(request.getRecipientSlackId())
            ).fetchOne();
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
     * 주문 ID로 검색
     * @param orderId : 검색할 주문의 ID
     * @return : 주문 ID 일치 여부 반환
     */
    private BooleanExpression orderId(UUID orderId) {
        return orderId != null ? delivery.orderId.eq(orderId) : null;
    }

    /**
     * 배송 상태로 검색
     * @param deliveryState : 검색할 배송 상태
     * @return : 배송 상태와 일치 여부 반환
     */
    private BooleanExpression deliveryState(DeliveryState deliveryState) {
        return deliveryState != null ? delivery.deliveryState.eq(deliveryState) : null;
    }

    /**
     * 배송 담당자 ID
     * @param shipperId : 검색할 배송 담당자 ID
     * @return : 배송 담당자 ID 일치 여부 반환
     */
    private BooleanExpression shipperId(UUID shipperId) {
        return shipperId != null ? delivery.shipperId.eq(shipperId) : null;
    }

    /**
     * 수령자 ID
     * @param recipientId : 검색할 수령자 ID
     * @return : 수령자 ID (user) 일치 여부 반환
     */
    private BooleanExpression recipientId(UUID recipientId) {
        return recipientId != null ? delivery.recipientId.eq(recipientId) : null;
    }

    /**
     * 수령자 슬랙 ID
     * @param recipientSlackId : 검색할 수령자의 Slack ID
     * @return : 수령자 Slack Id 일치 여부 반환
     */
    private BooleanExpression recipientSlackId(String recipientSlackId) {
        return recipientSlackId != null ? delivery.recipientSlackId.eq(recipientSlackId) : null;
    }

    /**
     * 권한 확인
     *
     * @param role            : 사용자 권한 확인
     * @param userId
     * @param hubManagerHubId
     * @return : 권한에 따른 결과 리턴
     */
    private BooleanExpression confirmRole(String role, UUID userId, UUID hubManagerHubId) {
        if (role.equals("ROLE_MASTER") || role.equals("ROLE_COMPANY_MANAGER")) {
            return null;
        } else {
            if (role.equals("ROLE_HUB_MANAGER")) {
                return delivery.arriveHubId.eq(hubManagerHubId)
                    .or(delivery.departHubId.eq(hubManagerHubId));
            }
            if (role.equals("ROLE_DELIVERY_MANAGER")) {
               return delivery.shipperId.eq(userId);
            }
        }
        return null;
    }

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
                orders.add(new OrderSpecifier<>(direction, delivery.createdAt));
                break;
            case "modifiedAt" :
                log.info("정렬 조건: updatedAt / 오름차순/내림차순" + direction);
                orders.add(new OrderSpecifier<>(direction, delivery.updatedAt));
                break;
            default :
                break;
        }
    }
}
