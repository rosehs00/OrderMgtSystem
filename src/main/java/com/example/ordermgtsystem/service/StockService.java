package com.example.ordermgtsystem.service;

import com.example.ordermgtsystem.dto.stock.StockResponse;
import com.example.ordermgtsystem.dto.stock.StockUpdateRequest;

public interface StockService {

    StockResponse createStock(Long productId, StockUpdateRequest request);

    StockResponse getStock(Long productId);

    StockResponse updateStock(Long productId, StockUpdateRequest request);
}
