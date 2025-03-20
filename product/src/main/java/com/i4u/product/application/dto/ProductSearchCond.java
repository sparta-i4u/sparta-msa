package com.i4u.product.application.dto;

import java.util.UUID;

//허브아이디, 회사아이디, 상품 이름으로 검색
public record ProductSearchCond(UUID hubId, UUID companyId, String name, boolean isDeleted) {}
