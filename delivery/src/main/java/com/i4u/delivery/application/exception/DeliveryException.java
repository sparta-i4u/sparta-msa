package com.i4u.delivery.application.exception;

import org.springframework.http.HttpStatus;

import com.i4u.common.exception.CustomException;

public class DeliveryException extends CustomException {
	public DeliveryException(String message, HttpStatus status) {
		super("300", message, status);
	}
}
