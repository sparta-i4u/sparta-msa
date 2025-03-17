package com.i4u.product.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse {

    private int code;
    private String message;
    private Object results;

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(int code, String message, Object results) {
        this.code = code;
        this.message = message;
        this.results = results;
    }
}