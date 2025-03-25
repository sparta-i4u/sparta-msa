//package com.i4u.product.presentation;
//
//import static org.junit.jupiter.api.Assertions.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.i4u.product.application.dto.request.ProductCreateRequest;
//import com.i4u.product.application.dto.request.ProductUpdateRequest;
//import com.i4u.product.application.dto.response.ProductResponse;
//import com.i4u.product.application.dto.response.ProductSearchResponse;
//import com.i4u.product.application.service.ProductService;
//import com.i4u.common.utils.CommonResponse;
//import com.i4u.product.domain.Product;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ProductController.class)
//class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Mock
//    private ProductService productService;
//
//    private UUID productId;
//    private ProductResponse productResponse;
//
//    @BeforeEach
//    void setUp() {
//        productId = UUID.randomUUID();
//        productResponse = new ProductResponse(productId, UUID.randomUUID(), UUID.randomUUID(), "테스트 상품", 10000, "설명");
//    }
//
//    @Test
//    void createProduct() throws Exception {
//        ProductCreateRequest request = new ProductCreateRequest(
//                UUID.randomUUID(), UUID.randomUUID(), "테스트 상품", 10000, "설명", 10
//        );
//        Mockito.when(productService.createProduct(any(), anyString(), anyString())).thenReturn(productResponse);
//
//        mockMvc.perform(post("/api/v1/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Id", "testUser")
//                        .header("X-User-Role", "MASTER")
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("상품 등록이 정상 수행되었습니다"));
//    }
//
//    @Test
//    void getProducts() throws Exception {
//        // Page<Product>를 Mock 객체로 생성
//        List<Product> productList = List.of(product);
//        Page<Product> productPage = new PageImpl<>(productList, Pageable.ofSize(10), 1);
//        ProductSearchResponse response = ProductSearchResponse.of(productPage);
//
//        Mockito.when(productService.findAll(anyInt(), anyInt(), any(), anyString(), anyString()))
//                .thenReturn(response);
//
//        mockMvc.perform(get("/api/v1/products/search")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .header("X-User-Id", "testUser")
//                        .header("X-User-Role", "MASTER"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("상품 목록이 정상 조회되었습니다"));
//    }
//
//    @Test
//    void findProductByKeyword() throws Exception {
//        List<Product> productList = List.of(product);
//        Page<Product> productPage = new PageImpl<>(productList, Pageable.ofSize(10), 1);
//        ProductSearchResponse response = ProductSearchResponse.of(productPage);
//
//        Mockito.when(productService.findProudctByKeyword(any(), anyInt(), anyInt(), any(), anyString(), anyString()))
//                .thenReturn(response);
//
//        mockMvc.perform(get("/api/v1/products/search/keyword")
//                        .param("keyword", "테스트")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .header("X-User-Id", "testUser")
//                        .header("X-User-Role", "MASTER"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("상품 키워드로 검색하였습니다"));
//    }
//
//    @Test
//    void updateProduct() throws Exception {
//        ProductUpdateRequest request = new ProductUpdateRequest("수정된 상품", "새 설명", 20000, 5);
//        Mockito.when(productService.updateProduct(any(), any(), anyString(), anyString())).thenReturn(productResponse);
//
//        mockMvc.perform(put("/api/v1/products/" + productId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Id", "testUser")
//                        .header("X-User-Role", "MASTER")
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("상품 정보가 수정되었습니다"));
//    }
//
//    @Test
//    void softDeleteProducts() throws Exception {
//        List<UUID> productIds = Collections.singletonList(productId);
//
//        mockMvc.perform(delete("/api/v1/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Id", "testUser")
//                        .header("X-User-Role", "MASTER")
//                        .content(objectMapper.writeValueAsString(productIds)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("상품이 정상적으로 삭제되었습니다"));
//    }
//}