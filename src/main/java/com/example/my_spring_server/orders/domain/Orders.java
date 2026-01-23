package com.example.my_spring_server.orders.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Orders {
    private Long id;
    private long userId;
    private int totalPrice;
    private LocalDateTime createdAt;

    private List<OrderItems> orderItems;

    protected Orders() { }

    protected Orders(long userId, List<OrderItems> orderItems) {
        this.userId = userId;
        this.orderItems = orderItems;
        totalPrice = orderItems.stream()
                .map(orderItem -> orderItem.getPriceAtOrder() * orderItem.getQuantity())
                .mapToInt(Integer::valueOf)
                .sum();
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }
}
