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
import com.i4u.client.AuthClient;
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
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipperService {

	private final ShipperRepository shipperRepository;
	private final HubClient hubClient;
	private final AuthClient authClient;
	// private final HubUserClient hubUserClient;

	@Value("${whole.hub.id}")
	private UUID wholeHubId;

	/**
	 * 배송 담당자 생성
	 *
	 * @param request : 배송 담당자 정보
	 * @param userId
	 * @param role
	 * @return : 생성한 배송 담당자 내용
	 */
	// MASTER, HUB_MANAGER(담당 허브)
	public ShipperCreateResponse createShipper(ShipperCreateRequest request, String userId, String role) {
		// 1. Role에 따른 배송 담당자 생성 권한 확인
		confirmRequestUser(userId, role, request.getHubId());

		// 2. [authClient] 사용자 쪽으로 사용자 검증 (ID, 삭제여부, 권한) 요청 보내기
		ConfirmUserResponse responseUser = authClient.confirmUser(UUID.fromString(userId));
		if (responseUser.getIsDeleted()) {
			throw new ShipperException("존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST);
		}
		if (! responseUser.getUserRole().equals("ROLE_DELIVERY_MANAGER")) {
			throw new ShipperException("권한이 배송 담당자가 아니므로 배송 담당자로 지정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		String shipperSlackId = responseUser.getUserSlackId();

		// 3. [hubClient] 허브 쪽으로 허브 검증 요청 보내기 -> 추후 Caching으로 전환 예정
		UUID hubId = request.getHubId();

		if (request.getShipperType().equals(ShipperType.HUB)) {
			// 타입이 허브 배송 담당자라면 hubId가 null 이므로 전체 허브 담당 ID 배정
			hubId = wholeHubId;
		}

		// 4. 배송 담당자 순서 지정
		Integer shipperOrder = shipperRepository.confirmShipperOrder(hubId);
		if (shipperOrder == 0) {
			throw new ShipperException("해당 허브에는 더 이상 배송 담당자를 배정할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. 배송 담당자 생성
		Shipper shipper = request.toShipper(shipperOrder, hubId, shipperSlackId);
		Shipper savedShipper = shipperRepository.save(shipper);

		return ShipperCreateResponse.fromShipper(savedShipper);
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 *
	 * @return : 조회된 배송 담당자들의 내용
	 */
	// MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 정보)
	public PagedModel<ShipperListResponse> getAllShippers(Pageable pageable, ShipperSearchRequest request,
														  String userId, String role) {
		// 1. Role에 따른 조회 권한 확인
		if (role.equals("ROLE_COMPANY_MANAGER")) {
			throw new ShipperException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		UUID hubManagerHubId = null;

		if (role.equals("ROLE_HUB_MANAGER")) {
			hubManagerHubId = hubClient.confirmHubFromUser(UUID.fromString(userId));
		}

		Page<ShipperListResponse> shippers = shipperRepository.searchShippers(pageable, request, userId, role, hubManagerHubId);
		PagedModel<ShipperListResponse> shipperDtoList = new PagedModel<>(shippers);
		return shipperDtoList;
	}

	/**
	 * 배송 담당자 단건 조회
	 *
	 * @return : 조회된 배송 담당자 내용
	 */
	// MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 정보)
	public ShipperGetOneResponse getOneShipper(UUID shipperId, String userId, String role) {
		// 1. 배송 담당자 검색
		Shipper shipper = findShipper(shipperId);

		// 2. Role에 따른 조회 권한 확인
		//    배송 담당자는 본인 정보만
		if (role.equals("ROLE_DELIVERY_MANAGER") && !shipper.getShipperId().equals(UUID.fromString(userId))) {
			System.out.println(shipper.getShipperId());
			System.out.println(UUID.fromString(userId));
			throw new ShipperException("조회 권한이 없습니다", HttpStatus.BAD_REQUEST);
		}

		//    허브 관리자는 본인의 허브에 소속된 정보만
		if (role.equals("ROLE_HUB_MANAGER")) {
			UUID hubManagerHubId = hubClient.confirmHubFromUser(UUID.fromString(userId));
			if (!hubManagerHubId.equals(shipper.getHubId())) {
				throw new ShipperException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
			}
		}

		//    그리고 업체 담당자면
		if (role.equals("ROLE_COMPANY_MANAGER")) {
			throw new ShipperException("조회 권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 배송 담당자 정보를 response에 담아서 반환
		return ShipperGetOneResponse.fromShipper(shipper);
	}

	/**
	 * 배송 담당자 수정 (배송 담당자가 속한 허브 ID & 배송 담당자 타입만 수정 가능)
	 *
	 * @param shipperId : 수정할 배송 담당자의 ID
	 * @param request   : 수정할 배송 담당자의 내용
	 * @param userId    : 요청한 사용자의 ID
	 * @param role      : 요청한 사용자의 권한
	 * @return : 수정된 배송 담당자 내용
	 */
	@Transactional // MASTER, HUB_MANAGER(담당 허브)
	public ShipperUpdateResponse updateShipper(UUID shipperId, ShipperUpdateRequest request, String userId, String role) {
		// 1. 배송 담당자 검색
		Shipper beforeShipper = findShipper(shipperId);

		// 2. Role에 따른 배송 담당자 수정 권한 확인
		confirmRequestUser(userId, role, request.getHubId());

		UUID hubId = (request.getHubId() == null) ? wholeHubId : request.getHubId();

		if (beforeShipper.getHubId().equals(request.getHubId())) {
			throw new ShipperException("변경 사항이 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 변경된 내용에 따라 배송 담당자 순서도 재지정 필요
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
	 * @param userId
	 * @param role
	 */
	@Transactional  // MASTER, HUB_MANAGER(담당 허브)
	public void deleteShipper(UUID shipperId, String userId, String role) {
		Shipper shipper = findShipper(shipperId);

		// 1. 권한 확인
		confirmRequestUser(userId, role, shipper.getHubId());

		// 2. 삭제 진행
		shipper.softDelete(UUID.fromString(userId));
	}

	// 권한 검증 함수
	private void confirmRequestUser(String userId, String role, UUID hubId) {
		switch (role) {
			case "ROLE_MASTER":
				break;

			case "ROLE_HUB_MANAGER":
				UUID hubManagerHubId = hubClient.confirmHubFromUser(UUID.fromString(userId));
				if (!hubManagerHubId.equals(hubId)) {
					throw new ShipperException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
				}
				break;

			default:
				throw new ShipperException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
		}
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