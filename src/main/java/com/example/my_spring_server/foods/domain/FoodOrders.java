package com.example.my_spring_server.foods.domain;

import java.util.Objects;

public record FoodOrders(Foods food, int quantity) {
    public FoodOrders {
        Objects.requireNonNull(food.getId());
    }

    public long foodId() {
        return food.getId();
    }

    public int foodPrice() {
        return food.getPrice();
    }
}
