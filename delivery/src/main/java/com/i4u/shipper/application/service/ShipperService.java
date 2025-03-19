package com.i4u.shipper.application.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.i4u.client.HubClient;
import com.i4u.client.UserClient;
import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.request.ShipperUpdateRequest;
import com.i4u.shipper.application.dtos.response.ShipperCreateResponse;
import com.i4u.shipper.application.dtos.response.ShipperGetOneResponse;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.application.dtos.response.ShipperUpdateResponse;
import com.i4u.shipper.application.exception.ShipperException;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;
import com.i4u.shipper.domain.repository.ShipperRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipperService {

	private final ShipperRepository shipperRepository;
	private final HubClient hubClient;
	private final UserClient userClient;

	@Value("${whole.hub.id}")
	private UUID wholeHubId;

	/* TODO: 1. 각 로직마다 Controller에서 받아온 사용자 정보 추가 필수
			 2. 타 도메인으로 요청을 보내고 응답을 받을 때, 필요한 정보가 무엇인지 확인 필수 */

	/**
	 * 배송 담당자 생성
	 *
	 * @param request : 배송 담당자 정보
	 * @return : 생성한 배송 담당자 내용
	 */
	public ShipperCreateResponse createShipper(ShipperCreateRequest request /*String userId, String userRole*/) {
		// 1. Role에 따른 배송 담당자 생성 권한 확인
		// {받아온 userId, userRole로 로직 추가 필요}

		// 2. [userClient] 사용자 쪽으로 사용자 검증 (ID, 삭제여부, 권한) 요청 보내기
		// ShipperUserResponse responseUser = userClient.confirmUser(
		// 	ShipperUserRequest.builder().userId(request.getUserId()).build()/* userId, userRole 혹은 JWT 넣어주기 */);
		// if (responseUser.getIsDeleted()) {
		// 	throw new ShipperException("존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST);
		// }
		// String userSlackId = responseUser.getUserSlackId();
		
		// 3. [hubClient] 허브 쪽으로 허브 검증 요청 보내기 -> 추후 Caching으로 전환 예정
		UUID hubId = request.getHubId();

		if (request.getShipperType().equals(ShipperType.HUB)) {
			// 타입이 허브 배송 담당자라면 hubId가 null 이므로 전체 허브 담당 ID 배정
			hubId = wholeHubId;
		} else {
			// 업체 배송 담당자의 경우만 검증 필요
			Boolean hubExists = hubClient.confirmHub(hubId).getBody().getData().getIsDeleted();

			if (!hubExists){
				throw new ShipperException("존재하지 않는 허브입니다.", HttpStatus.BAD_REQUEST);
			}
		}

		// 4. 배송 담당자 순서 지정
		Integer shipperOrder = shipperRepository.confirmShipperOrder(hubId);
		if (shipperOrder == 0) {
			throw new ShipperException("해당 허브에는 더 이상 배송 담당자를 배정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. 배송 담당자 생성
		Shipper shipper = request.toShipper(shipperOrder, hubId /*, userSlackId*/);
		Shipper savedShipper = shipperRepository.save(shipper);

		return ShipperCreateResponse.fromShipper(savedShipper);
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 *
	 * @return : 조회된 배송 담당자들의 내용
	 */
	public PagedModel<ShipperListResponse> getAllShippers(Pageable pageable, ShipperSearchRequest request) {
		// 1. Role에 따른 조회 권한 확인 {받아온 userId, userRole 넘기기 로직 추가 필요}
		Page<ShipperListResponse> shippers = shipperRepository.searchShippers(pageable, request);
		PagedModel<ShipperListResponse> shipperDtoList = new PagedModel<>(shippers);
		return shipperDtoList;
	}

	/**
	 * 배송 담당자 단건 조회
	 *
	 * @return : 조회된 배송 담당자 내용
	 */
	public ShipperGetOneResponse getOneShipper(UUID shipperId) {
		// 1. 배송 담당자 검색
		Shipper shipper = findShipper(shipperId);

		// 2. Role에 따른 조회 권한 확인 {받아온 userId, userRole 넘기기 로직 추가 필요}
		// MASTER 제외, HUBMANAGER라면 담당 hub 만 .. 이러면 이 사용자가 담당하는 허브가 무엇인지 검증이 필요하지 않나 ?
		// DeliveryManager라면 userId와 shipperId가 동일한지만 확인하면 됨

		// 3. response로 반환
		return ShipperGetOneResponse.fromShipper(shipper);
	}

	/**
	 * 배송 담당자 수정
	 *
	 * @param shipperId : 수정할 배송 담당자의 ID
	 * @param request : 수정할 배송 담당자의 내용
	 * @return : 수정된 배송 담당자 내용
	 */
	@Transactional
	public ShipperUpdateResponse updateShipper(UUID shipperId, ShipperUpdateRequest request) {
		/* 배송 담당자가 속한 허브 ID & 배송 담당자 타입만 수정 가능 */
		// 1. 배송 담당자 검색
		Shipper beforeShipper = findShipper(shipperId);

		// 2. Role에 따른 배송 담당자 수정 권한 확인
		// {받아온 userId, userRole로 로직 추가 필요}
		UUID hubId = (request.getHubId() == null) ? wholeHubId : request.getHubId();

		if (beforeShipper.getHubId().equals(request.getHubId())) {
			throw new ShipperException("변경 사항이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. [userClient] 사용자 쪽으로 사용자 검증 (ID, 삭제여부, 권한) 요청 보내기
		// ShipperUserResponse responseUser = userClient.confirmUser(
		// 	ShipperUserRequest.builder().userId(beforeShipper.getUserId()).build()/* userId, userRole 혹은 JWT 넣어주기 */);
		// if (responseUser.getIsDeleted()) {
		// 	throw new ShipperException("존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST);
		// }

		// 4. [hubClient] 허브로 검증 요청 전송
		if (!hubId.equals(wholeHubId)) {
			// 업체 배송 담당자의 경우만 검증 필요
			Boolean hubExists = hubClient.confirmHub(hubId).getBody().getData().getIsDeleted();

			if (!hubExists){
				throw new ShipperException("존재하지 않는 허브입니다.", HttpStatus.BAD_REQUEST);
			}
		}

		// 5. 변경된 내용에 따라 배송 담당자 순서도 재지정 필요
		Integer shipperOrder = shipperRepository.confirmShipperOrder(hubId);
		if (shipperOrder == 0) {
			throw new ShipperException("해당 허브에는 더 이상 배송 담당자를 배정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 6. 배송 담당자 내용 수정
		Shipper updateingShipper = request.toShipper(shipperOrder, hubId);
		beforeShipper.updateShipper(updateingShipper);
		return ShipperUpdateResponse.fromShipper(beforeShipper);
	}

	/**
	 * 배송 담당자 삭제
	 *
	 * @param shipperId : 삭제할 배송 담당자의 ID
	 */
	@Transactional
	public void deleteShipper(UUID shipperId) {
		Shipper shipper = findShipper(shipperId);

		// TODO : 변경된 Basic에서 받아가서 softDelete 적용하기
		// shipper.softDelete(/*지금 요청한 사용자의 ID*/);
	}
	
	/**
	 * 배송 담당자 검색
	 *
	 * @param shipperId : 검색할 배송 담당자의 ID
	 * @return : 검색한 배송 담당자
	 */
	private Shipper findShipper(UUID shipperId) {
		return shipperRepository.findById(shipperId)
			.orElseThrow(() -> new ShipperException("해당 배송 담당자를 찾을 수 없습니다", HttpStatus.BAD_REQUEST));
	}

}