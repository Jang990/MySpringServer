package com.example.my_spring_server.users.presentation.dto;

import com.example.my_spring_server.users.domain.Users;

public record UserResponse(long userId, String name, int balance) {
    public static UserResponse from(Users users) {
        return new UserResponse(
                users.getId(),
                users.getName(),
                users.getBalance()
        );
    }
}
