package com.example.ordermgtsystem.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Product
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Stock
    STOCK_NOT_FOUND("재고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_STOCK("해당 상품의 재고가 이미 존재합니다.", HttpStatus.CONFLICT),

    // Order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS("현재 상태에서는 해당 작업을 수행할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
