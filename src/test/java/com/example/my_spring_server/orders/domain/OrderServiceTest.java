package com.example.my_spring_server.orders.domain;

import com.example.my_spring_server.MyReflectionTestUtils;
import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.users.domain.Users;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    OrderService orderService = new OrderService();

    Users users1;

    Foods food1;
    Foods food2;

    @BeforeEach
    void beforeEach() {
        users1 = new Users("아무개", 15_000);
        MyReflectionTestUtils.setField(users1, "id", 1L);

        food1 = new Foods("짜장면", 1_000, 10);
        MyReflectionTestUtils.setField(food1, "id", 111L);

        food2 = new Foods("짬뽕", 2_000, 5);
        MyReflectionTestUtils.setField(food2, "id", 222L);
    }

    @Test
    @DisplayName("주문 생성 시 음식_재고감소 + 사용자_잔액_감소")
    void test1() {
        List<FoodOrders> foodOrders = List.of(
                new FoodOrders(food1, 3), // 1000원 * 3
                new FoodOrders(food2, 1) // 2000원
        );

        Orders order = orderService.order(users1, foodOrders);

        // food1의 orderItem 총액 = 3 * 1_000
        assertEquals(food1.getId(), order.getOrderItems().get(0).getFoodId());
        assertEquals(3, order.getOrderItems().get(0).getQuantity());
        assertEquals(1_000, order.getOrderItems().get(0).getPriceAtOrder());
        assertEquals(3_000, order.getOrderItems().get(0).getTotalPrice());

        // food1의 재고 감소 10 -> 7
        assertEquals(7, food1.getStock());

        // food2의 orderItem 총액 = 1 * 2_000
        assertEquals(food2.getId(), order.getOrderItems().get(1).getFoodId());
        assertEquals(1, order.getOrderItems().get(1).getQuantity());
        assertEquals(2_000, order.getOrderItems().get(1).getPriceAtOrder());
        assertEquals(2_000, order.getOrderItems().get(1).getTotalPrice());

        // food2의 재고 감소 5 -> 4
        assertEquals(4, food2.getStock());

        // 총 결제 금액 = 3000 + 2000
        assertEquals(5_000, order.getTotalPrice());

        // 사용자 잔액 15_000 -> 10_000
        assertEquals(users1.getId(), order.getUserId());
        assertEquals(10_000, users1.getBalance());
    }

    @Test
    @DisplayName("재고가 너무 적은 경우 예외처리")
    public void test2() {
        List<FoodOrders> foodOrders = List.of(
                new FoodOrders(food1, 1),
                new FoodOrders(food2, 6) // 재고가 5개 -> 부족
        );

        assertThrows(IllegalStateException.class, () -> orderService.order(users1, foodOrders));
    }

    @Test
    @DisplayName("사용자 잔액이 부족한 경우")
    public void test3() {
        // 16_000원 주문
        List<FoodOrders> foodOrders = List.of(
                new FoodOrders(food1, 6), // 1000원 * 6 = 6000원
                new FoodOrders(food2, 5) // 2000원 * 5 = 10000원
        );

        assertThrows(IllegalStateException.class, () -> orderService.order(users1, foodOrders));
    }

}