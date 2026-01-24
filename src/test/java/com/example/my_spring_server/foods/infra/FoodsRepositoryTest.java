package com.example.my_spring_server.foods.infra;

import com.example.my_spring_server.MySQLConfig;
import com.example.my_spring_server.foods.domain.Foods;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodsRepositoryTest {
    FoodsRepository foodsRepository = new FoodsRepository(new MySQLConfig());

    @Test
    void 음식_저장() {
        Foods food = new Foods("짜장면", 5000, 30);

        Foods result = foodsRepository.save(food);

        assertNotNull(result.getId());
        assertEquals("짜장면", result.getName());
        assertEquals(5000, result.getPrice());
        assertEquals(30, result.getStock());

        Foods dbFood = foodsRepository.findById(food.getId());
        assertEquals(result.getName(), dbFood.getName());
        assertEquals(result.getPrice(), dbFood.getPrice());
        assertEquals(result.getStock(), dbFood.getStock());
    }

    @Test
    void 찾을_수_없는_음식_오류() {
        assertThrows(IllegalArgumentException.class, () -> foodsRepository.findById(-1));
    }

    @Test
    void 음식_재고_변경() {
        Foods testFood = foodsRepository.save(new Foods("짬뽕", 7000, 1000));
        foodsRepository.changeStock(testFood.getId(), 50);

        Foods dbFood = foodsRepository.findById(testFood.getId());
        assertEquals(50, dbFood.getStock());
    }

}