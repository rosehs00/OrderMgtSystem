package com.example.ordermgtsystem.service;

import com.example.ordermgtsystem.dto.order.OrderCreateRequest;
import com.example.ordermgtsystem.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderCreateRequest request);

    OrderResponse getOrder(Long id);

    List<OrderResponse> getAllOrders();

    OrderResponse confirmOrder(Long id);

    OrderResponse cancelOrder(Long id);

    OrderResponse completeOrder(Long id);
}
