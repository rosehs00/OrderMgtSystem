package com.example.ordermgtsystem.dto.stock;

import com.example.ordermgtsystem.domain.Stock;

import java.time.LocalDateTime;

public record StockResponse(
        Long id,
        Long productId,
        String productName,
        int quantity,
        LocalDateTime updatedAt
) {
    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getProduct().getId(),
                stock.getProduct().getName(),
                stock.getQuantity(),
                stock.getUpdatedAt()
        );
    }
}
