package com.i4u.shipper.controller;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.request.ShipperUpdateRequest;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;
import com.i4u.client.HubClient;
import com.i4u.client.AuthClient;
import com.i4u.shipper.domain.repository.ShipperRepository;
import com.i4u.shipper.infrastructure.persistence.ShipperRepositoryImpl;
import com.i4u.shipper.presentation.dtos.request.ShipperUserRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperHubResponse;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // post(), get(), put(), delete() 등
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  // status().isOk(), status().isBadRequest(), jsonPath() 등


@AutoConfigureMockMvc
// @AutoConfigureRestDocs
@SpringBootTest(classes = DeliveryApplication.class)
@Transactional
public class ShipperControllerTest {

	// @Autowired
	// private MockMvc mockMvc;
	//
	// @Autowired
	// private ObjectMapper objectMapper;
	//
	// @Autowired
	// private JPAQueryFactory jpaQueryFactory;
	//
	// @MockitoBean
	// private HubClient hubClient;
	//
	// @MockitoBean
	// private AuthClient authClient;
	//
	// @MockitoBean
	// private ShipperRepository shipperRepository;
	//
	// @Test
	// @DisplayName("배송 담당자 생성 성공")
	// public void createShipperTest() throws Exception {
	// 	// given
	// 	UUID hubId = UUID.randomUUID();
	// 	UUID userId = UUID.randomUUID();
	// 	UUID hubMasterId = UUID.randomUUID();
	// 	ShipperCreateRequest request = createCompanyShipperRequest(hubId, userId);
	// 	Shipper shipper = makeMockShipper(userId, hubId);
	//
	// 	// when
	// 	Mockito.when(shipperRepository.confirmShipperOrder(Mockito.any(UUID.class))).thenReturn(1);
	// 	Mockito.when(shipperRepository.save(Mockito.any(Shipper.class))).thenReturn(shipper);
	// 	Mockito.when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(hubId);
	// 	Mockito.when(authClient.confirmUser(Mockito.any(UUID.class))).thenReturn(createFeignClientUserResponse(userId));
	//
	// 	// when&then
	// 	mockMvc.perform(
	// 				post("/api/v1/shippers")
	// 					// 지금 배송 담당자 생성 요청을 하는 사람은 허브 담당자
	// 					.header("X-User-Id", hubMasterId)
	// 					.header("X-User-Role", "ROLE_HUB_MANAGER")
	// 					.contentType(MediaType.APPLICATION_JSON)
	// 					.content(new ObjectMapper().writeValueAsString(request)))
	// 			.andExpect(status().isOk()
	// 			// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
	// 			// 	requestFields(
	// 			// 		fieldWithPath("hubId").description("허브 ID"),
	// 			// 		fieldWithPath("userId").description("사용자 ID")
	// 			// 	)
	// 			);
	// }
	//
	// @Test
	// @DisplayName("배송 담당자 전체 조회 성공")
	// public void getAllShippersTest() throws Exception {
	// 	// given
	// 	UUID hubId = UUID.randomUUID();
	// 	UUID hubMasterId = UUID.randomUUID();
	//
	// 	// when
	// 	Mockito.when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(hubId);
	//
	// 	// when & then
	// 	mockMvc.perform(
	// 			get("/api/v1/shippers")
	// 				// 지금 배송 담당자 조회 요청을 하는 사람은 마스터
	// 				.header("X-User-Id", hubMasterId)
	// 				.header("X-User-Role", "ROLE_MASTER")
	// 			)
	// 		.andExpect(status().isOk()
	// 			// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
	// 			// 	requestFields(
	// 			// 		fieldWithPath("hubId").description("허브 ID"),
	// 			// 		fieldWithPath("userId").description("사용자 ID")
	// 			// 	)
	// 		);
	// }
	//
	// @Test
	// @DisplayName("배송 담당자 단건 조회 성공")
	// public void getOneShipperTest() throws Exception {
	// 	// given
	// 	UUID hubId = UUID.randomUUID();
	// 	UUID userId = UUID.randomUUID();
	// 	Shipper shipper = makeMockShipper(userId, hubId);
	//
	// 	System.out.println("userId : " + userId);
	// 	System.out.println("shipperId : " + shipper.getShipperId());
	//
	// 	// when
	// 	Mockito.when(shipperRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.ofNullable(shipper));
	// 	Mockito.when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(hubId);
	//
	// 	// when & then
	// 	mockMvc.perform(
	// 			get("/api/v1/shippers/" + userId)
	// 				// 지금 배송 담당자 조회 요청을 하는 사람은 배송 담당자
	// 				.header("X-User-Id", userId)
	// 				.header("X-User-Role", "ROLE_DELIVERY_MANAGER")
	// 		)
	// 		.andExpect(status().isOk()
	// 			// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
	// 			// 	requestFields(
	// 			// 		fieldWithPath("hubId").description("허브 ID"),
	// 			// 		fieldWithPath("userId").description("사용자 ID")
	// 			// 	)
	// 		);
	// }
	//
	// @Test
	// @DisplayName("배송 담당자 수정 성공")
	// public void updateShipperTest() throws Exception {
	// 	// given
	// 	UUID hubId = UUID.randomUUID();
	// 	UUID newHubId = UUID.randomUUID();
	// 	UUID userId = UUID.randomUUID();
	// 	UUID hubMasterId = UUID.randomUUID();
	// 	Shipper shipper = makeMockShipper(userId, hubId);
	//
	// 	ShipperUpdateRequest request = updateCompanyShipperRequest(newHubId);
	//
	// 	System.out.println("userId : " + userId);
	// 	System.out.println("shipperId : " + shipper.getShipperId());
	//
	// 	// when
	// 	Mockito.when(shipperRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.ofNullable(shipper));
	// 	Mockito.when(shipperRepository.confirmShipperOrder(Mockito.any(UUID.class))).thenReturn(1);
	// 	Mockito.when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(newHubId);
	//
	// 	// when & then
	// 	mockMvc.perform(
	// 			put("/api/v1/shippers/" + userId)
	// 				// 지금 배송 담당자 수정 요청을 하는 사람은 허브 담당자
	// 				.header("X-User-Id", hubMasterId)
	// 				.header("X-User-Role", "ROLE_HUB_MANAGER")
	// 				.contentType(MediaType.APPLICATION_JSON)
	// 				.content(new ObjectMapper().writeValueAsString(request)))
	// 		.andExpect(status().isOk()
	// 			// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
	// 			// 	requestFields(
	// 			// 		fieldWithPath("hubId").description("허브 ID"),
	// 			// 		fieldWithPath("userId").description("사용자 ID")
	// 			// 	)
	// 		);
	// }
	//
	// @Test
	// @DisplayName("배송 담당자 삭제 성공")
	// public void deleteShipperTest() throws Exception {
	// 	// given
	// 	UUID hubId = UUID.randomUUID();
	// 	UUID userId = UUID.randomUUID();
	// 	UUID hubMasterId = UUID.randomUUID();
	// 	Shipper shipper = makeMockShipper(userId, hubId);
	// 	System.out.println("userId : " + userId);
	// 	System.out.println("shipperId : " + shipper.getShipperId());
	//
	// 	// when
	// 	Mockito.when(shipperRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.ofNullable(shipper));
	// 	Mockito.when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(hubId);
	//
	// 	// when & then
	// 	mockMvc.perform(
	// 			delete("/api/v1/shippers/" + userId)
	// 				// 지금 배송 담당자 생성 요청을 하는 사람은 허브 담당자
	// 				.header("X-User-Id", hubMasterId)
	// 				.header("X-User-Role", "ROLE_HUB_MANAGER")
	// 			)
	// 		.andExpect(status().isOk()
	// 			// .andDo(document("delivery-manager-create", // RestDocs 연동 부분
	// 			// 	requestFields(
	// 			// 		fieldWithPath("hubId").description("허브 ID"),
	// 			// 		fieldWithPath("userId").description("사용자 ID")
	// 			// 	)
	// 		);
	// }
	//
	// /**
	//  * 업체 배송 담당자 생성 Req
	//  *
	//  * @param hubId : HubId
	//  * @param userId : 사용자 Id
	//  * @return : Req
	//  */
	// ShipperCreateRequest createCompanyShipperRequest(UUID hubId, UUID userId) {
	// 	return ShipperCreateRequest.builder()
	// 		.hubId(hubId)
	// 		.shipperType(ShipperType.COMPANY)
	// 		.userId(userId)
	// 		.build();
	// }
	//
	// /**
	//  * 업체 배송 담당자 수정 Req
	//  *
	//  * @param hubId : HubId
	//  * @return : Req
	//  */
	// ShipperUpdateRequest updateCompanyShipperRequest(UUID hubId) {
	// 	return ShipperUpdateRequest.builder()
	// 		.hubId(hubId)
	// 		.shipperType(ShipperType.COMPANY)
	// 		.build();
	// }
	//
	// /**
	//  *
	//  *
	//  * @param userId
	//  * @return
	//  */
	// ConfirmUserResponse createFeignClientUserResponse(UUID userId) {
	// 	return ConfirmUserResponse.builder()
	// 		.userId(userId).userRole("ROLE_DELIVERY_MANAGER")
	// 		.userSlackId("SDFESFLKJ").isDeleted(false).build();
	// }
	//
	// Shipper makeMockShipper(UUID userId, UUID hubId) {
	// 	return Shipper.builder()
	// 		.shipperId(userId)
	// 		.hubId(hubId)
	// 		.shipperType(ShipperType.COMPANY)
	// 		.userId(userId)
	// 		.userSlackId("SLDFKJEDF")
	// 		.build();
	// }
	//
	// ShipperUserRequest createFeignClientUser(UUID userId) {
	// 	return ShipperUserRequest.builder().userId(userId).build();
	// }
	//
	// ResponseEntity<CommonResponse<ShipperHubResponse>> createFeignClientHubResponse(UUID hubId) {
	// 	return ResponseEntity.ok(
	// 		CommonResponse.success(
	// 			ShipperHubResponse.builder().hubId(hubId).isDeleted(false).build(),
	// 			"허브 검증 성공"
	// 		)
	// 	);
	// }


}

