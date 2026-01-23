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

        assertEquals("짜장면", result.getName());
        assertEquals(5000, result.getPrice());
        assertEquals(30, result.getStock());
    }

    @Test
    void 찾을_수_없는_음식_오류() {
        assertThrows(IllegalArgumentException.class, () -> foodsRepository.findById(-1));
    }

    @Test
    void ID로_음식_찾기() {
        Foods testFood = new Foods("짜장면", 5000, 30);
        foodsRepository.save(testFood);

        Foods result = foodsRepository.findById(testFood.getId());

        assertEquals(testFood.getId(), result.getId());
        assertEquals("짜장면", result.getName());
        assertEquals(5000, result.getPrice());
        assertEquals(30, result.getStock());
    }

}