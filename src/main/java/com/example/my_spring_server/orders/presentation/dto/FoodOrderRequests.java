package com.example.my_spring_server.orders.presentation.dto;

import java.util.List;

public record FoodOrderRequests(List<FoodOrderRequest> foodOrders) {
    public List<Long> foodIds() {
        return foodOrders.stream()
                .mapToLong(FoodOrderRequest::foodId)
                .boxed()
                .toList();
    }
}
