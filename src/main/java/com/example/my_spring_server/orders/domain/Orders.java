package com.example.my_spring_server.orders.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Orders {
    private Long id;
    private long userId;
    private int totalPrice;
    private LocalDateTime createdAt;

    private List<OrderItems> orderItems;

    protected Orders(long userId, List<OrderItems> orderItems) {
        this.userId = userId;
        totalPrice = orderItems.stream()
                .map(orderItem -> orderItem.getPriceAtOrder() * orderItem.getQuantity())
                .mapToInt(Integer::valueOf)
                .sum();
        createdAt = LocalDateTime.now();
    }
}
