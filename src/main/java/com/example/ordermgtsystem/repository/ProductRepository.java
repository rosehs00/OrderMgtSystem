package com.example.ordermgtsystem.repository;

import com.example.ordermgtsystem.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
