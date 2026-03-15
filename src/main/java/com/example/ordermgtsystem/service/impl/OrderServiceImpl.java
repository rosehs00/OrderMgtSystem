package com.example.ordermgtsystem.service.impl;

import com.example.ordermgtsystem.domain.Order;
import com.example.ordermgtsystem.domain.OrderItem;
import com.example.ordermgtsystem.domain.Product;
import com.example.ordermgtsystem.domain.Stock;
import com.example.ordermgtsystem.dto.order.OrderCreateRequest;
import com.example.ordermgtsystem.dto.order.OrderResponse;
import com.example.ordermgtsystem.exception.BusinessException;
import com.example.ordermgtsystem.exception.ErrorCode;
import com.example.ordermgtsystem.exception.OutOfStockException;
import com.example.ordermgtsystem.repository.OrderRepository;
import com.example.ordermgtsystem.repository.ProductRepository;
import com.example.ordermgtsystem.repository.StockRepository;
import com.example.ordermgtsystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    public OrderResponse createOrder(OrderCreateRequest request) {
        Order order = Order.create(generateOrderNumber());

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            reserveStockWithPessimisticLock(itemRequest.productId(), itemRequest.quantity());

            OrderItem item = OrderItem.create(product, itemRequest.quantity());
            order.addOrderItem(item);
        }

        return OrderResponse.from(orderRepository.save(order));
    }

    private void reserveStockWithPessimisticLock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        if (stock.getQuantity() < quantity) {
            throw new OutOfStockException(productId, quantity, stock.getQuantity());
        }

        stock.decrease(quantity);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.from(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    public OrderResponse confirmOrder(Long id) {
        Order order = orderRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.confirm();
        return OrderResponse.from(order);
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.cancel();

        // 취소 시 재고 복원
        for (OrderItem item : order.getOrderItems()) {
            Stock stock = stockRepository.findByProductIdWithLock(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
            stock.increase(item.getQuantity());
        }

        return OrderResponse.from(order);
    }

    @Override
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.complete();
        return OrderResponse.from(order);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniquePart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uniquePart;
    }
}
