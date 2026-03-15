package com.example.ordermgtsystem.controller;

import com.example.ordermgtsystem.dto.common.ApiResponse;
import com.example.ordermgtsystem.dto.stock.StockResponse;
import com.example.ordermgtsystem.dto.stock.StockUpdateRequest;
import com.example.ordermgtsystem.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(stockService.createStock(productId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StockResponse>> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStock(productId)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<StockResponse>> updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(stockService.updateStock(productId, request)));
    }
}
