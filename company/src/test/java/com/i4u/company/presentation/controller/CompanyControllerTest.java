package com.i4u.company.presentation.controller;

import com.i4u.company.application.dto.request.CompanyCreateRequest;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponse;
import com.i4u.company.application.dto.response.CompanySearchResponse;
import com.i4u.company.application.service.CompanyService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    @Test
    @DisplayName("업체 생성 API 테스트")
    void createCompany() throws Exception {
        CompanyCreateRequest request = new CompanyCreateRequest("43f8f0cd-8e11-4fd9-bdcb-862bb53966c9");
        CompanyResponse response = new CompanyResponse(UUID.randomUUID(), "43f8f0cd-8e11-4fd9-bdcb-862bb53966c9");

        when(companyService.createCompany(any(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "test-user")
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Company"));
    }

    @Test
    @DisplayName("업체 전체 조회 API 테스트")
    void getCompany() throws Exception {
        CompanySearchResponse response = new CompanySearchResponse(Collections.emptyList());
        when(companyService.findAll(anyInt(), anyInt(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/companies/search")
                        .param("page", "0")
                        .param("size", "10")
                        .header("X-User-Id", "test-user")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("업체 수정 API 테스트")
    void updateCompany() throws Exception {
        UUID companyId = UUID.randomUUID();
        CompanyUpdateRequest request = new CompanyUpdateRequest("Updated Company");
        CompanyResponse response = new CompanyResponse(companyId, "Updated Company");

        when(companyService.updateCompany(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/companies/" + companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "test-user")
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Company"));
    }

    @Test
    @DisplayName("업체 삭제 API 테스트")
    void softDeleteCompanies() throws Exception {
        UUID companyId = UUID.randomUUID();

        doNothing().when(companyService).softDeleteCompanies(any(), any(), any());

        mockMvc.perform(delete("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(companyId)))
                        .header("X-User-Id", "test-user")
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk());
    }
}
