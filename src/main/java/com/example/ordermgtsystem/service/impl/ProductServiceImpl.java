package com.example.ordermgtsystem.service.impl;

import com.example.ordermgtsystem.domain.Product;
import com.example.ordermgtsystem.dto.product.ProductCreateRequest;
import com.example.ordermgtsystem.dto.product.ProductResponse;
import com.example.ordermgtsystem.exception.BusinessException;
import com.example.ordermgtsystem.exception.ErrorCode;
import com.example.ordermgtsystem.repository.ProductRepository;
import com.example.ordermgtsystem.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = Product.create(request.name(), request.description(), request.price());
        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }
}
