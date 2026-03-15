package com.example.ordermgtsystem.exception;

public class OutOfStockException extends BusinessException {

    public OutOfStockException(Long productId, int requested, int available) {
        super(
                ErrorCode.INSUFFICIENT_STOCK,
                "재고가 부족합니다. productId=" + productId + ", 요청=" + requested + ", 현재재고=" + available
        );
    }
}