package com.example.ordermgtsystem.service.impl;

import com.example.ordermgtsystem.domain.Product;
import com.example.ordermgtsystem.domain.Stock;
import com.example.ordermgtsystem.dto.stock.StockResponse;
import com.example.ordermgtsystem.dto.stock.StockUpdateRequest;
import com.example.ordermgtsystem.exception.BusinessException;
import com.example.ordermgtsystem.exception.ErrorCode;
import com.example.ordermgtsystem.repository.ProductRepository;
import com.example.ordermgtsystem.repository.StockRepository;
import com.example.ordermgtsystem.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Override
    public StockResponse createStock(Long productId, StockUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (stockRepository.findByProductId(productId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_STOCK);
        }

        Stock stock = Stock.create(product, request.quantity());
        return StockResponse.from(stockRepository.save(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
        return StockResponse.from(stock);
    }

    @Override
    public StockResponse updateStock(Long productId, StockUpdateRequest request) {
        Stock stock = stockRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
        stock.updateQuantity(request.quantity());
        return StockResponse.from(stock);
    }
}
