package com.i4u.common.utils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse<T> {

	private static final String SUCCESS_CODE = "S000";
	private static final String FAIL_CODE = "F000";
	private static final String CREATED_CODE = "C000";

	private String code;
	private T data;
	private String message;

	public static <T> CommonResponse<T> success(T data, String message) {
		return CommonResponse.<T>builder()
			.code(SUCCESS_CODE)
			.data(data)
			.message(message)
			.build();
	}

	public static <T> CommonResponse<T> fail() {
		return CommonResponse.<T>builder()
			.code(FAIL_CODE)
			.data(null)
			.message(null)
			.build();
	}

	public static <T> CommonResponse<T> created(T data, String message) {
		return CommonResponse.<T>builder()
				.code(CREATED_CODE)
				.data(data)
				.message(message)
				.build();
	}
}