package com.example.my_spring_server.foods.domain;

import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequests;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public record FoodOrders(Foods food, int quantity) {
    public static List<FoodOrders> from(FoodOrderRequests requests, List<Foods> foods) {
        Map<Long, Foods> foodMap = foods.stream()
                .collect(Collectors.toMap(Foods::getId, Function.identity()));

        return requests.foodOrders()
                .stream()
                .map(request -> {
                    long requestFoodId = request.foodId();
                    Foods food = foodMap.get(requestFoodId);
                    if(food == null)
                        throw new IllegalArgumentException("찾을 수 없는 음식 ID가 포함돼 있습니다. foodId=%d".formatted(requestFoodId));
                    return new FoodOrders(food, request.quantity());
                })
                .toList();
    }

    public FoodOrders {
        Objects.requireNonNull(food.getId());
    }

    public long foodId() {
        return food.getId();
    }

    public int foodPrice() {
        return food.getPrice();
    }

    public void decreaseStock() {
        food.decreaseStock(quantity);
    }

    public void assertEnoughStock() {
        food.assertEnoughStock(quantity);
    }
}
