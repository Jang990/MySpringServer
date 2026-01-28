package com.example.my_spring_server.orders.application;

import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.foods.infra.FoodsRepository;
import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.orders.domain.OrderService;
import com.example.my_spring_server.orders.domain.Orders;
import com.example.my_spring_server.orders.infra.OrderRepository;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequests;
import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FoodOrderService {
    private final MyDataSource myDataSource;
    private final OrderService orderService;

    private final OrderRepository orderRepository;
    private final FoodsRepository foodsRepository;
    private final UsersRepository usersRepository;

    public FoodOrderService(
            MyDataSource myDataSource,
            OrderService orderService,
            OrderRepository orderRepository,
            FoodsRepository foodsRepository,
            UsersRepository usersRepository
    ) {
        this.myDataSource = myDataSource;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.foodsRepository = foodsRepository;
        this.usersRepository = usersRepository;
    }


    public void order(long userId, FoodOrderRequests foodOrderRequests) {
        Users user = usersRepository.findById(userId);
        List<Foods> foods = foodsRepository.findAll(foodOrderRequests.foodIds());
        List<FoodOrders> foodOrders = FoodOrders.from(foodOrderRequests, foods);

        try(Connection conn = myDataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);
                Orders order = orderService.order(user, foodOrders);

                orderRepository.save(conn, order);
                usersRepository.updateBalance(conn, user.getId(), user.getBalance());
                for (Foods food : foods)
                    foodsRepository.updateStock(conn, food.getId(), food.getStock());
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
