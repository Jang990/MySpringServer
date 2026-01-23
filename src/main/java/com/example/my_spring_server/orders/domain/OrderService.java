package com.example.my_spring_server.orders.domain;

import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.users.domain.Users;

import java.util.LinkedList;
import java.util.List;

public class OrderService {
    public Orders order(Users user, List<FoodOrders> foodOrders) {
        List<OrderItems> orderItems = createOrderItems(foodOrders);
        return new Orders(user.getId(), orderItems);
    }

    private List<OrderItems> createOrderItems(List<FoodOrders> foodOrders) {
        List<OrderItems> orderItems = new LinkedList<>();
        for (FoodOrders foodOrder : foodOrders) {
            OrderItems orderItem = new OrderItems(
                    foodOrder.foodId(),
                    foodOrder.foodPrice(),
                    foodOrder.quantity()
            );
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
