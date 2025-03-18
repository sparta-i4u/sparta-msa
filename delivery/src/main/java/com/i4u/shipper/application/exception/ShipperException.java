package com.i4u.shipper.application.exception;

import org.springframework.http.HttpStatus;

import com.i4u.common.exception.CustomException;

public class ShipperException extends CustomException {
	public ShipperException(String message, HttpStatus status) {
		// Shipper ErrorCode는 600번대로 고정
		super("400", message, status);
	}
}