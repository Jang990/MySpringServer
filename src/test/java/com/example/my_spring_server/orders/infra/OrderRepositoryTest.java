package com.example.my_spring_server.orders.infra;

import com.example.my_spring_server.MySQLConfig;
import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.foods.infra.FoodsRepository;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.orders.domain.OrderItems;
import com.example.my_spring_server.orders.domain.OrderService;
import com.example.my_spring_server.orders.domain.Orders;
import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {
    MySQLConfig mySQLConfig = new MySQLConfig();

    UsersRepository usersRepository = new UsersRepository(mySQLConfig, new MyJdbcTemplate());
    FoodsRepository foodsRepository = new FoodsRepository(mySQLConfig);
    OrderRepository orderRepository = new OrderRepository(mySQLConfig);


    @Test
    void 주문정보_저장_조회_테스트() {
        Users users = new Users("김아무개", 5000);
        usersRepository.save(users);

        Foods foods1 = new Foods("떡볶이", 1000, 10);
        Foods foods2 = new Foods("짬뽕", 2000, 5);
        foodsRepository.save(foods1);
        foodsRepository.save(foods2);

        List<FoodOrders> foodOrders = List.of(
                new FoodOrders(foods1, 10),
                new FoodOrders(foods2, 20)
        );

        OrderService orderService = new OrderService();
        Orders order = orderService.order(users, foodOrders);
        orderRepository.save(order);

        Orders result = orderRepository.findById(order.getId());

        assertEquals(order.getId(), result.getId());
        assertEquals(order.getUserId(), result.getUserId());
        assertEquals(order.getTotalPrice(), result.getTotalPrice());
        assertEquals(order.getCreatedAt().truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));

        assertEquals(order.getOrderItems().size(), result.getOrderItems().size());
        assertOrderItems(order.getOrderItems().get(0), result.getOrderItems().get(0));
        assertOrderItems(order.getOrderItems().get(1), result.getOrderItems().get(1));
    }

    private static void assertOrderItems(OrderItems order1, OrderItems order2) {
        assertEquals(order1.getId(), order2.getId());
        assertEquals(order1.getOrderId(), order2.getOrderId());
        assertEquals(order1.getFoodId(), order2.getFoodId());
        assertEquals(order1.getPriceAtOrder(), order2.getPriceAtOrder());
        assertEquals(order1.getQuantity(), order2.getQuantity());
    }

}