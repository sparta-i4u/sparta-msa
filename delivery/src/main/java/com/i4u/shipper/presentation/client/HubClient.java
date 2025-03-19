package com.i4u.shipper.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.presentation.dtos.request.ShipperHubRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperHubResponse;

@FeignClient(name = "hub", path = "/api/v1/hubs") // hub에 해당하는 path는 gateway에서 매핑됨
public interface HubClient {

	// Shipper -> Hub 로 해당 허브가 존재하는지 여부 확인
	@GetMapping("/{hubId}/shipper")
	ResponseEntity<CommonResponse<ShipperHubResponse>> confirmHub(@PathVariable UUID hubId/*userId, userRole or JWT 필요*/);

}
