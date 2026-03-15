package com.example.ordermgtsystem.service;

import com.example.ordermgtsystem.dto.product.ProductCreateRequest;
import com.example.ordermgtsystem.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getAllProducts();
}
