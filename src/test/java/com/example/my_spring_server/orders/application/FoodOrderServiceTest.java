package com.example.my_spring_server.orders.application;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.MySQLConfig;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.foods.infra.FoodsRepository;
import com.example.my_spring_server.my.datasource.DriverManagerDataSource;
import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.datasource.transaction.MyTransactionAwareDataSourceProxy;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.my.transaction.MyTransactionManager;
import com.example.my_spring_server.orders.domain.OrderService;
import com.example.my_spring_server.orders.infra.OrderRepository;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequest;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequests;
import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoodOrderServiceTest {
    DBConfig mysqlConfig = new MySQLConfig();
    MyDataSource myTxDataSource = new MyTransactionAwareDataSourceProxy(new DriverManagerDataSource(mysqlConfig));

    OrderService orderService = new OrderService();

    MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(myTxDataSource);
    OrderRepository orderRepository = new OrderRepository(myJdbcTemplate);
    UsersRepository usersRepository = new UsersRepository(myJdbcTemplate);
    FoodsRepository foodsRepository = new MockFoodRepository(myJdbcTemplate); // 예외 발생 객체

    FoodOrderService foodOrderService = new FoodOrderService(
            orderService,
            orderRepository, foodsRepository, usersRepository,
            new MyTransactionManager(myTxDataSource)
    );

    // 롤백 테스트를 위한 예외를 발생시키는 클래스
    static class MockFoodRepository extends FoodsRepository {
        public MockFoodRepository(MyJdbcTemplate myJdbcTemplate) {
            super(myJdbcTemplate);
        }

        @Override
        public void updateStock(long userId, int balance) {
            throw new MockException("트랜잭션 테스트를 위한 예외");
        }

        static class MockException extends RuntimeException {
            public MockException(String message) {
                super(message);
            }
        }
    }

    @Test
    @DisplayName("주문 트랜잭션 롤백 테스트")
    void test() {
        // given
        Users users = new Users("김아무개", 5000);
        usersRepository.save(users);

        Foods foods1 = new Foods("떡볶이", 100, 10);
        Foods foods2 = new Foods("짬뽕", 200, 5);
        foodsRepository.save(foods1);
        foodsRepository.save(foods2);

        // when - then
        assertThrows(MockFoodRepository.MockException.class,
                () -> foodOrderService.order(
                        users.getId(),
                        new FoodOrderRequests(
                                List.of(
                                        new FoodOrderRequest(foods1.getId(), 5),
                                        new FoodOrderRequest(foods2.getId(), 3)
                                )
                        )
                )
        );

        // then - 롤백 체크
        assertEquals(5000, usersRepository.findById(users.getId()).getBalance());
        assertEquals(10, foodsRepository.findById(foods1.getId()).getStock());
        assertEquals(5, foodsRepository.findById(foods2.getId()).getStock());
    }

}