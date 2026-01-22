package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.MySQLConfig;
import com.example.my_spring_server.users.domain.Users;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersRepositoryTest {
    UsersRepository repository = new UsersRepository(new MySQLConfig());

    @Test
    void Users_저장하기() {
        Users result = repository.save(new Users("아무개", 10_000));

        assertNotNull(result.getId());
        assertEquals("아무개", result.getName());
        assertEquals(10_000, result.getBalance());
    }

}