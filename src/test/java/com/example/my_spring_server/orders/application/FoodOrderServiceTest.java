package com.example.my_spring_server.orders.application;

import com.example.my_spring_server.AppConfig;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.foods.infra.FoodsRepository;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequest;
import com.example.my_spring_server.orders.presentation.dto.FoodOrderRequests;
import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
class FoodOrderServiceTest {
    @Autowired private UsersRepository usersRepository;
    @Autowired private FoodsRepository foodsRepository;

    @Autowired private FoodOrderService foodOrderService;

    @Test
    @DisplayName("주문 트랜잭션 롤백 테스트")
    void test1() {
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

    @Configuration
    @Import(AppConfig.class)
    static class TestConfig {

        @Bean
        @Primary
        public FoodsRepository foodsRepository(MyJdbcTemplate jdbc) {
            // 롤백 테스트를 위한 예외를 발생시키는 클래스
            return new MockFoodRepository(jdbc);
        }
    }

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

}