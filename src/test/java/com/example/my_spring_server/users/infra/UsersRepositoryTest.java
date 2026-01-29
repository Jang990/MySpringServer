package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.MySQLConfig;
import com.example.my_spring_server.my.datasource.DriverManagerDataSource;
import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.users.domain.Users;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersRepositoryTest {
    MyDataSource dataSource = new DriverManagerDataSource(new MySQLConfig());
    UsersRepository repository = new UsersRepository(dataSource, new MyJdbcTemplate());

    @Test
    void Users_저장_조회() {
        Users createdUser = repository.save(new Users("아무개", 10_000));
        assertNotNull(createdUser.getId());
        assertEquals("아무개", createdUser.getName());
        assertEquals(10_000, createdUser.getBalance());

        Users dbUser = repository.findById(createdUser.getId());
        assertEquals(createdUser.getId(), dbUser.getId());
        assertEquals(createdUser.getName(), dbUser.getName());
        assertEquals(createdUser.getBalance(), dbUser.getBalance());
    }

    @Test
    void Users_조회하기() {
        Users testUser = repository.save(new Users("아무개", 10_000));
        long testUserId = testUser.getId();

        Users result = repository.findById(testUserId);
        assertEquals(testUserId, result.getId());
        assertEquals("아무개", result.getName());
        assertEquals(10_000, result.getBalance());
    }

    @Test
    void 찾을_수_없는_사용자_조회() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(-1));
    }

    @Test
    void 사용자_잔액_변경() {
        Users testUser = repository.save(new Users("아무개", 10_000));
        repository.updateBalance(testUser.getId(), 3_000);

        Users result = repository.findById(testUser.getId());
        assertEquals(3_000, result.getBalance());
    }

}