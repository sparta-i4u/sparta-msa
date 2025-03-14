package com.i4u.order.presentation.exception;

import org.springframework.http.HttpStatus;

import com.i4u.common.exception.CustomException;

public class OrderException extends CustomException {
	public OrderException(String message, HttpStatus status) {
		super("600", message, status);
	}
}
