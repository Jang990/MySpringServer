package com.example.my_spring_server.orders.application;

import com.example.my_spring_server.foods.domain.FoodOrders;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.foods.infra.FoodsRepository;
import com.example.my_spring_server.orders.domain.OrderService;
import com.example.my_spring_server.orders.domain.Orders;
import com.example.my_spring_server.orders.infra.OrderRepository;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequests;
import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;

import java.util.List;

public class FoodOrderService {
    private final OrderService orderService;

    private final OrderRepository orderRepository;
    private final FoodsRepository foodsRepository;
    private final UsersRepository usersRepository;

    public FoodOrderService(
            OrderService orderService,
            OrderRepository orderRepository,
            FoodsRepository foodsRepository,
            UsersRepository usersRepository
    ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.foodsRepository = foodsRepository;
        this.usersRepository = usersRepository;
    }


    public void order(long userId, FoodOrderRequests foodOrderRequests) {
        Users user = usersRepository.findById(userId);
        List<Foods> foods = foodsRepository.findAll(foodOrderRequests.foodIds());
        List<FoodOrders> foodOrders = FoodOrders.from(foodOrderRequests, foods);

        Orders order = orderService.order(user, foodOrders);

        orderRepository.save(order);
        usersRepository.updateBalance(user.getId(), user.getBalance());
        for (Foods food : foods)
            foodsRepository.updateStock(food.getId(), food.getStock());
    }
}
