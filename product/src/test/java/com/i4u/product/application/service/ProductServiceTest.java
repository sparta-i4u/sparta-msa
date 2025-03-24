//package com.i4u.product.application.service;
//
//import com.i4u.order.presentation.client.CompanyClient;
//import com.i4u.order.presentation.client.HubClient;
//import com.i4u.product.application.dto.request.ProductCreateRequest;
//import com.i4u.product.application.dto.request.ProductUpdateRequest;
//import com.i4u.product.application.dto.response.ProductResponse;
//import com.i4u.product.application.dto.response.ProductSearchResponse;
//import com.i4u.product.domain.Product;
//import com.i4u.product.domain.repository.ProductQueryRepository;
//import com.i4u.product.domain.repository.ProductRepository;
//
//import com.i4u.product.exception.ProductNotFoundException;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static com.i4u.product.domain.QProduct.product;
//import static org.assertj.core.api.Assertions.*;
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
//
//
//// 테스트 : 상품 등록 성공
//// 테스트 : 존재하지 않는 상품 ID 조회 시 예외 발생
//// 테스트 : 전체 상품 조회(페이지네이션) 성공
//// 테스트 : 상품 수정 성공
//// 테스트 : 상품 삭제 성공
//// 테스트 : 상품 삭제 실패
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ProductQueryRepository productQueryRepository;
//
//    @Mock
//    private HubClient hubClient;
//
//    @Mock
//    private CompanyClient companyClient;
//
//    @InjectMocks  //목으로 생성된 레파지토리 주입
//    private ProductService productService;
//
//    private UUID productId;
//    private UUID companyId;
//    private UUID hubId;  // ✅ hubId 선언 추가
//
////    public Product(final UUID hubId, final UUID companyId, final String name, final Integer price , final String content, final Integer count) {
////        this.hubId = hubId;
////        this.companyId = companyId;
////        this.name =  name;
////        this.content = content;
////        this.price = price;
////        this.count = count;
////        this.isDeleted = false;
////    }
//
//    @BeforeEach
//    void setUp() {  //각각의 테스트코드가 실행되기 전 수행되는 메소드
//        productId = UUID.randomUUID(); // UUID 생성
//        companyId = UUID.randomUUID();
//        hubId = UUID.randomUUID();
//        Product product = new Product(hubId, companyId, "상품", 10000, "상품설명",10); // 상품 객체 생성
//        ProductUpdateRequest request = new ProductUpdateRequest("새 상품명", "새 설명", 1000, 10);
//    }
//
//    @AfterEach
//    void tearDown() {  //각각의 테스트 코드가 실행된 후 실행
//    }
//
//    @BeforeAll //모든 테스트 코드가 실행되기 전 최초로 실행
//    static void beforeAll(){
//    }
//
//    @AfterAll  //모든 테스트코드가 수행된 후 마지막으로 수행
//    static void afterAll(){}
//
//
//    @Test
//    @DisplayName("상품 생성 성공 테스트코드") //테스트 내용 한눈에 알아보게 네이밍
//    void testCreateProduct_success() {
//        // given: 상품 등록 요청 데이터 생성
//        UUID hubId = UUID.randomUUID();
//        UUID companyId= UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        String role = "MASTER";
//        ProductCreateRequest requestDto = new ProductCreateRequest(
//                hubId, // hubId
//                companyId, // companyId
//                "테스트 상품", // 상품명
//                1000, // 가격
//                "테스트 상품 설명", // 설명
//                10 // 수량
//        );
//
//        // HubClient mock 설정
//        when(hubClient.getHubIdFromOrder(any(UUID.class))).thenReturn(requestDto.hubId());  // mock HubClient
//
//        // CompanyClient mock 설정
//        when(companyClient.getCompanyId(any(UUID.class))).thenReturn(companyId);
//
//
//        // ProductService에서 반환할 상품 객체 생성
//        Product mockProduct = new Product(requestDto.hubId(), requestDto.companyId(), requestDto.name(),
//                requestDto.price(), requestDto.content(), requestDto.count());
//        mockProduct.setId(UUID.randomUUID());  // 생성된 상품의 ID 설정
//
//        // ProductRepository가 반환할 가짜 저장된 상품 객체
//        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);
//
//        // when: createProduct 메서드 호출
//        ProductResponse response = productService.createProduct(requestDto, userId.toString(), role);
//
//        // then: 반환된 DTO 값 검증
//        assertThat(response).isNotNull();
//        assertThat(response.getId()).isNotNull();
//        assertThat(response.getName()).isEqualTo("테스트 상품");
//        assertThat(response.getPrice()).isEqualTo(1000);
//        assertThat(response.getContent()).isEqualTo("테스트 상품 설명");
//
//        // productRepository가 1번 호출되었는지 확인
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    //상품 전체 조회
//    @Test
//    public void testFindAll() {
//        // given: 테스트 데이터 설정
//        int page = 1;
//        int size = 10;
//        String sort = "name";
//        String userId = "user123";
//        String role = "ROLE_HUB_MANAGER";
//        UUID hubId = UUID.randomUUID(); // mock role에 따른 hubId
//        List<Product> products = List.of(
//                new Product(UUID.randomUUID(), hubId,  "Product 1", 1000, "Description 1", 10),
//                new Product(UUID.randomUUID(), hubId,  "Product 2", 2000, "Description 2", 15)
//        );
//        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), products.size());
//
//        // mock behavior
//        // confirmRole을 ProductService 내에서 직접 처리하는 경우
//        when(productService.confirmRole(userId, role)).thenReturn(hubId);  // confirmRole 메서드 mock 처리
//        when(productQueryRepository.findAll(any(Pageable.class), eq(role), eq(hubId))).thenReturn(productPage); // findAll 호출 시 반환값 설정
//
//        // when: 메서드 호출
//        ProductSearchResponse response = productService.findAll(page, size, sort, userId, role);
//
//        // then: 결과 검증
//        assertThat(response).isNotNull();
//        assertThat(response.products()).hasSize(2);  // 2개의 상품이 반환됨을 확인
//        assertThat(response.totalPages()).isEqualTo(1);
//        assertThat(response.totalElements()).isEqualTo(2L);
//
//        // verify: productPage의 map 메서드가 호출되었는지 확인
//        verify(productPage, times(1)).map(any());
//    }
//
//    @Test
//    void updateProduct_success_companyManager() {
//        String userId = "user123";
//        String role = "ROLE_COMPANY_MANAGER";
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productService.confirmRole(userId, role)).thenReturn(companyId);
//
//        ProductResponse response = productService.updateProduct(productId, request, userId, role);
//
//        //응답 확인
//        assertNotNull(response);
//
//        //상품 이름 업데이트 됐는지 확인
//        assertEquals("새 상품명", product.getName());
//    }
//
//    //상품 update
//    @Test
//    void updateProduct_success_hubManager() {
//        String userId = "user456";
//        String role = "ROLE_HUB_MANAGER";
//
//        ProductUpdateRequest request = new ProductUpdateRequest("새 상품명", "새 설명", 1000, 10);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productService.confirmRole(userId, role)).thenReturn(hubId);
//
//        ProductResponse response = productService.updateProduct(productId, request, userId, role);
//
//        assertNotNull(response);
//        assertEquals("새 상품명", product.getName());
//    }
//
//    @Test
//    void updateProduct_fail_another_role() {
//        String userId = "user101";
//        String role = "ROLE_HUB_MANAGER";
//        UUID anotherHubId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//
//        ProductUpdateRequest request = new ProductUpdateRequest(
//                "테스트 상품", // 상품명
//                "테스트 상품 설명",
//                1000, // 가격
//                // 설명
//                10 // 수량
//        );
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productService.confirmRole(userId, role)).thenReturn(anotherHubId);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                productService.updateProduct(productId, request, userId, role));
//    }
//
//    @Test
//    void findProductById_success() {
//        when(productQueryRepository.findById(productId)).thenReturn(Optional.of(product));
//
//        Product foundProduct = productService.findProductById(productId);
//
//        assertNotNull(foundProduct);
//        assertEquals(productId, foundProduct.getId());
//    }
//
//    @Test
//    void findProductById_fail_not_found() {
//        UUID productId = UUID.randomUUID();
//
//        when(productQueryRepository.findById(productId)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () ->
//                productService.findProductById(productId));
//    }
//
//    //상품 삭제
//    @Test
//    void softDeleteProducts_success_hubManager() {
//        String userId = "user222";
//        String role = "ROLE_HUB_MANAGER";
//        List<UUID> productIds = List.of(UUID.randomUUID(), UUID.randomUUID());
//        List<Product> products = List.of(new Product(productIds.get(0), "상품1", companyId, hubId),
//                new Product(productIds.get(1), "상품2", companyId, hubId));
//
//        when(productRepository.findAllById(productIds)).thenReturn(products);
//        when(productService.confirmRole(userId, role)).thenReturn(hubId);
//
//        assertDoesNotThrow(() -> productService.softDeleteProducts(productIds, userId, role));
//    }
//
//    @Test
//    void softDeleteProducts_fail_not_found_role() {
//        String userId = "user333";
//        String role = "ROLE_COMPANY_MANAGER";
//        List<UUID> productIds = List.of(UUID.randomUUID());
//
//        when(productService.confirmRole(userId, role)).thenReturn(companyId);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                productService.softDeleteProducts(productIds, userId, role));
//    }
//
//    @Test
//    void softDeleteProducts_fail_not_found_Products() {
//        String userId = "user444";
//        String role = "ROLE_HUB_MANAGER";
//        List<UUID> productIds = List.of(UUID.randomUUID());
//
//        when(productRepository.findAllById(productIds)).thenReturn(Collections.emptyList());
//        when(productService.confirmRole(userId, role)).thenReturn(hubId);
//
//        assertThrows(ProductNotFoundException.class, () ->
//                productService.softDeleteProducts(productIds, userId, role));
//    }
//}
//
