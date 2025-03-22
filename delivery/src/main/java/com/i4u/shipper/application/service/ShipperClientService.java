package com.i4u.shipper.application.service;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.i4u.shipper.application.exception.ShipperException;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.repository.ShipperRepository;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipperClientService {

	private final ShipperRepository shipperRepository;

	// HubId, DeliveryOrder 순으로 저장해두고, 해당 허브에 마지막으로 배송한 배송 담당자가 누구인지 저장해둠
	// 추후 캐싱으로도 전환 가능
	private HashMap<UUID, Integer> shippersLastOrder = new HashMap<>();

	/**
	 * 배송 담당자 지정
	 *
	 * @param recipientHubId : 지정할 배송 담당자 내용
	 * @return : 배정된 배송 담당자 반환
	 */
	public DeliveryShipperResponse assignShipper(UUID recipientHubId) {
		System.out.println("Shipper's ClientService RecipientHubId : " + recipientHubId);
		// 1. request에 도착 HubId에 해당하는 배송자가 있는지 확인
		Integer lastOrder = shippersLastOrder.get(recipientHubId);

		Shipper shipper = null;
		if (lastOrder == null) {
			System.out.println("아무도없어서 여기로 배정되어야 함");
			// repository에서 새로 배송 담당자 탐색
			shipper = shipperRepository.assignShipper(recipientHubId);
		} else {
			// repository에서 (lastOrder보다 큰 값 중 가장 작은 값) 탐색
			shipper = shipperRepository.assignNewShipper(recipientHubId, lastOrder);
		}

		// Using @ExceptionHandler com.i4u.shipper.application.exception.ShipperExceptionHandler#errorResponse(ShipperException)
		// 2025-03-22T15:05:06.303+09:00 ERROR 32340 --- [delivery] [io-19050-exec-1] c.i.s.a.e.ShipperExceptionHandler        : [ErrorCode] = 400 , [ErrorMessage] = 배송 담당자가 없습니다.

		// 3. Repository에 업체 담당 배송자가 없다면 Exception
		if (shipper == null) {
			throw new ShipperException("배송 담당자가 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 있으면 Hashmap에 저장
		shippersLastOrder.put(shipper.getHubId(), shipper.getShipperOrder());

		// 4. 찾아낸 배송 담당자 Return
		return DeliveryShipperResponse.builder()
			.shipperId(shipper.getShipperId()).recipientHubId(shipper.getHubId()).isDeleted(false)
			.build();
	}

}
