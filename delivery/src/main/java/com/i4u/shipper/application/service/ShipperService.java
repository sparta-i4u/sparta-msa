package com.i4u.shipper.application.service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.request.ShipperUpdateRequest;
import com.i4u.shipper.application.dtos.response.ShipperCreateResponse;
import com.i4u.shipper.application.dtos.response.ShipperGetOneResponse;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.application.dtos.response.ShipperUpdateResponse;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.repository.ShipperRepository;
import com.i4u.shipper.presentation.exception.ShipperException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipperService {

	private final ShipperRepository shipperRepository;
	// TODO : 타 도메인으로 요청을 보내고 응답을 받을 때, 필요한 정보가 무엇인지 확인 필수
	// TODO : 배송 순서 지정 시, 허브 담당 배송자라면 전체에 10명만, 업체 배송 담당자면 허브 마다 10명인 것을 주의하기

	/**
	 * 배송 담당자 생성
	 *
	 * @param request : 배송 담당자 정보
	 * @return : 생성한 배송 담당자 내용
	 */
	public ShipperCreateResponse createShipper(ShipperCreateRequest request) {
		// TODO : [user] 사용자 쪽으로 사용자 검증 (ID, 삭제여부, 권한) 요청 보내기
		// 사용자 검증이 안됐다면 Exception Throw
		// userClient.confirmUser(request.getUserId());
		
		// TODO : [hub] 허브 쪽으로 허브 검증 (삭제 여부) 요청 보내기
		// TODO : 추후 Caching으로 전환 예정
		// 허브 검증에 실패했다면 Exception Throw
		// hubClient.confirmHub(request.getHubId());

		// TODO : [changeOrder] hub 검증이 완료되었다면, 해당 허브 혹은 업체 담당자의 배송 순서를 어떻게 지정할지 고민해보기
		Integer shipperOrder = 1;
		Shipper shipper = request.toShipper(shipperOrder);

		Shipper savedShipper = shipperRepository.save(shipper);
		return ShipperCreateResponse.toDto(savedShipper);
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 *
	 * @return : 조회된 배송 담당자들의 내용
	 */
	public List<ShipperListResponse> getAllShippers(Pageable pageable, ShipperSearchRequest request) {
		// TODO : CustomRepo에서 Pagination적용하는 코드로 변경하기 (+검색기능 구현 필수)
		PagedModel<ShipperListResponse> shippers = shipperRepository.searchShippers(pageable, request);

		List<Shipper> shipperList = shipperRepository.findAll();
		return shipperList.stream().map(ShipperListResponse::toDto)
			              .collect(Collectors.toList());
	}

	/**
	 * 배송 담당자 단건 조회
	 *
	 * @return : 조회된 배송 담당자 내용
	 */
	public ShipperGetOneResponse getOneShipper(UUID shipperId) {
		// 배송 담당자 검색
		Shipper shipper = findShipper(shipperId);

		// response로 반환
		return ShipperGetOneResponse.toDto(shipper);
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
		Shipper beforeShipper = findShipper(shipperId);

		// TODO : [hub] 허브 로 검증 요청 필요 (배송 담당자 사용자 변경 로직은 현재 X)
		// 없다면 Exception
		// hubClient.confirmHub(request.getHubId());

		// TODO : [changeOrder] 변경된 허브에 따른 배송 순서 변경 필요
		Integer shipperOrder = 1;
		Shipper updateingShipper = request.toShipper(shipperOrder);
		beforeShipper.updateShipper(updateingShipper);

		return ShipperUpdateResponse.toDto(beforeShipper);
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

	// TODO : 배송 담당자 타입에 따른 배송 순서 지정하는 메서드 추가
	// 생각하는 것은 shipperRepository에서 isDeleted가 false인 개수를 찾아 그 개수에 +1 하기
	// custom repo를 사용해도 될 듯
	
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