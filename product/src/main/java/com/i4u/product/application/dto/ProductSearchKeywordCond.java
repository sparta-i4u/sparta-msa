package com.i4u.product.application.dto;

import java.util.UUID;

public record ProductSearchKeywordCond (UUID hubId, UUID companyId, String name, boolean isDeleted ){
}