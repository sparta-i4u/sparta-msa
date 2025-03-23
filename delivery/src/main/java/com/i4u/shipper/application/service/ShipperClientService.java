package com.i4u.shipper.application.service;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
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
public class ShipperClientService implements ShipperClient {

	private final ShipperRepository shipperRepository;

	// HubId, DeliveryOrder 순으로 저장해두고, 해당 허브에 마지막으로 배송한 배송 담당자가 누구인지 저장해둠
	private HashMap<UUID, Integer> shippersLastOrder = new HashMap<>();

	// 캐싱
	private final RedisTemplate<String, Integer> redisTemplate;
	private static final String CACHE_PREFIX = "shippers:lastOrder:";

	/**
	 * 배송 담당자 지정
	 *
	 * @param recipientHubId : 지정할 배송 담당자 내용
	 * @return : 배정된 배송 담당자 반환
	 */
	@Override
	public DeliveryShipperResponse assignShipper(UUID recipientHubId) {
		log.info("Shipper's ClientService RecipientHubId: {}", recipientHubId);

		// 1. Redis에서 캐싱된 lastOrder 조회 (UUID → String 변환)
		String cacheKey = CACHE_PREFIX + recipientHubId.toString();
		Integer lastOrder = redisTemplate.opsForValue().get(cacheKey);

		Shipper shipper = null;
		if (lastOrder == null) {
			log.info("캐싱된 배송자가 없어서 새로 배정해야 함");
			// 2. repository에서 새 배송 담당자 탐색
			shipper = shipperRepository.assignShipper(recipientHubId);
		} else {
			log.info("Redis에서 가져온 lastOrder: {}", lastOrder);
			// 3. repository에서 (lastOrder보다 큰 값 중 가장 작은 값) 탐색
			shipper = shipperRepository.assignNewShipper(recipientHubId, lastOrder);
		}

		// 4. 배송 담당자가 없으면 예외 발생
		if (shipper == null) {
			throw new ShipperException("배송 담당자가 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. Redis에 저장 (lastOrder 업데이트)
		redisTemplate.opsForValue().set(cacheKey, shipper.getShipperOrder());

		// 6. 찾아낸 배송 담당자 Return
		return DeliveryShipperResponse.builder()
			.shipperId(shipper.getShipperId())
			.recipientHubId(shipper.getHubId())
			.shipperEmail(shipper.getUserEmail())
			.shipperSlackId(shipper.getUserSlackId())
			.isDeleted(false)
			.build();
	}

	// 캐시 삭제 (필요하면 추가)
	public void evictCache(UUID recipientHubId) {
		String cacheKey = CACHE_PREFIX + recipientHubId.toString();
		redisTemplate.delete(cacheKey);
	}

}
