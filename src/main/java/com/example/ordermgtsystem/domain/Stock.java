package com.example.ordermgtsystem.domain;

import com.example.ordermgtsystem.exception.BusinessException;
import com.example.ordermgtsystem.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Stock create(Product product, int initialQuantity) {
        Stock stock = new Stock();
        stock.product = product;
        stock.quantity = initialQuantity;
        return stock;
    }

    public void decrease(int amount) {
        if (this.quantity < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.quantity -= amount;
    }

    public void increase(int amount) {
        this.quantity += amount;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
