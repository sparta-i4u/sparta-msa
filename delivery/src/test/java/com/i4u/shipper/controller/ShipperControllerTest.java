package com.i4u.shipper.controller;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4u.DeliveryApplication;
import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.domain.entity.ShipperType;
import com.i4u.client.HubClient;
import com.i4u.client.AuthClient;
import com.i4u.shipper.presentation.dtos.request.ShipperUserRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperHubResponse;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // post(), get(), put(), delete() 등
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  // status().isOk(), status().isBadRequest(), jsonPath() 등


@AutoConfigureMockMvc
// @AutoConfigureRestDocs
@SpringBootTest(classes = DeliveryApplication.class)
@Transactional
public class ShipperControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private HubClient hubClient;

	@MockitoBean
	private AuthClient authClient;

	@Test
	@DisplayName("배송 담당자 생성 성공")
	public void createShipperTest() throws Exception {
		// given
		UUID hubId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		ShipperCreateRequest request = createCompanyShipperRequest(hubId, userId);

		// when
		// Mockito.when(hubClient.confirmHub(hubId)).thenReturn(createFeignClientHubResponse(hubId));
		// Mockito.when(authClient.confirmUser(createFeignClientUser(userId))).thenReturn(createFeignClientUserResponse(userId));

		// when&then
		// mockMvc.perform(
		// 			post("/api/v1/shippers")
		// 				// .header(HttpHeaders.AUTHORIZATION, "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
		// 				.contentType(MediaType.APPLICATION_JSON)
		// 				.content(new ObjectMapper().writeValueAsString(request)))
		// 		.andExpect(status().isOk()
				// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
				// 	requestFields(
				// 		fieldWithPath("hubId").description("허브 ID"),
				// 		fieldWithPath("userId").description("사용자 ID")
				// 	)
				// );
	}

	@Test
	@DisplayName("배송 담당자 전체 조회 성공")
	public void getAllShippersTest() throws Exception {

	}

	@Test
	@DisplayName("배송 담당자 전체 조회 성공")
	public void getOneShipperTest() throws Exception {

	}


	/**
	 * 업체 배송 담당자 생성 Req
	 *
	 * @param hubId : HubId
	 * @param userId : 사용자 Id
	 * @return : Req
	 */
	ShipperCreateRequest createCompanyShipperRequest(UUID hubId, UUID userId) {
		return ShipperCreateRequest.builder()
			.hubId(hubId)
			.shipperType(ShipperType.COMPANY)
			.userId(userId)
			.build();
	}

	ShipperUserRequest createFeignClientUser(UUID userId) {
		return ShipperUserRequest.builder().userId(userId).build();
	}

	ConfirmUserResponse createFeignClientUserResponse(UUID userId) {
		return ConfirmUserResponse.builder().userId(userId).isDeleted(false).build();
	}

	ResponseEntity<CommonResponse<ShipperHubResponse>> createFeignClientHubResponse(UUID hubId) {
		return ResponseEntity.ok(
			CommonResponse.success(
				ShipperHubResponse.builder().hubId(hubId).isDeleted(false).build(),
				"허브 검증 성공"
			)
		);
	}


}

