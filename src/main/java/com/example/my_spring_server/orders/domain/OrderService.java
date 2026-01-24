package com.example.my_spring_server.orders.domain;

import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.users.domain.Users;

import java.util.LinkedList;
import java.util.List;

public class OrderService {
    // 음식 개수 감소 + 사용자 잔액 감소 + 주문 저장
    public Orders order(Users user, List<FoodOrders> foodOrders) {
        List<OrderItems> orderItems = createOrderItems(foodOrders);
        Orders order = new Orders(user.getId(), orderItems);

        // 검증 먼저 -> 값 변경 중간에 터지면 기존 객체들의 값이 유지돼야하기 때문이다.
        user.assertCanPay(order.getTotalPrice());
        foodOrders.forEach(FoodOrders::assertEnoughStock);

        // 값 변경
        user.decreaseBalance(order.getTotalPrice());
        for (FoodOrders foodOrder : foodOrders)
            foodOrder.decreaseStock();
        return order;
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
