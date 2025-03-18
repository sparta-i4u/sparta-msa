package com.i4u.hub.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.i4u.common.entity.Basic;
import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import com.i4u.hub.presentation.dtos.ShipperHubResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubClientService {

	private final HubRepository hubRepository;

	/**
	 * 허브 검증 요청
	 * 
	 * @param hubId : 검증할 허브 ID
	 * @return : 검증된 허브 내용
	 */
	public ShipperHubResponse confirmHubFromShipper(UUID hubId) {
		Hub hub = hubRepository.findById(hubId).filter(h -> !h.getIsDeleted())
			.orElseThrow(() -> new IllegalArgumentException("해당 허브가 존재하지 않습니다. "));

		ShipperHubResponse response = ShipperHubResponse.builder()
			.hubId(hub.getHubId()).isDeleted(false).build();

		return response;
	}

}
