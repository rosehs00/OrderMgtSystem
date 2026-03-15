package com.example.ordermgtsystem.service.impl;

import com.example.ordermgtsystem.domain.Order;
import com.example.ordermgtsystem.domain.Product;
import com.example.ordermgtsystem.domain.Stock;
import com.example.ordermgtsystem.dto.order.OrderCreateRequest;
import com.example.ordermgtsystem.dto.order.OrderResponse;
import com.example.ordermgtsystem.exception.OutOfStockException;
import com.example.ordermgtsystem.repository.OrderRepository;
import com.example.ordermgtsystem.repository.ProductRepository;
import com.example.ordermgtsystem.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("BVA: 재고 1개(경계값)이고 주문수량 1개면 주문이 성공한다")
    void placeOrder_shouldSucceed_whenStockIsOne() {
        Long productId = 1L;
        Product product = Product.create("콜라", "탄산음료", BigDecimal.valueOf(2000));
        Stock stock = Stock.create(product, 1);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(stockRepository.findByProductIdWithLock(productId)).thenReturn(Optional.of(stock));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderCreateRequest request = new OrderCreateRequest(
                List.of(new OrderCreateRequest.OrderItemRequest(productId, 1))
        );

        OrderResponse response = orderService.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.orderItems()).hasSize(1);
        assertThat(stock.getQuantity()).isZero();
        verify(stockRepository).findByProductIdWithLock(productId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("BVA: 재고 0개(경계값)이고 주문수량 1개면 OutOfStockException이 발생한다")
    void placeOrder_shouldFail_whenStockIsZero() {
        Long productId = 1L;
        Product product = Product.create("콜라", "탄산음료", BigDecimal.valueOf(2000));
        Stock stock = Stock.create(product, 0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(stockRepository.findByProductIdWithLock(productId)).thenReturn(Optional.of(stock));

        OrderCreateRequest request = new OrderCreateRequest(
                List.of(new OrderCreateRequest.OrderItemRequest(productId, 1))
        );

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("재고가 부족합니다");

        assertThat(stock.getQuantity()).isZero();
        verify(stockRepository).findByProductIdWithLock(productId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
