package com.example.my_spring_server.users.application;

import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.presentation.dto.UserCreationRequest;

public class UserService {
    public Long createUser(UserCreationRequest request) {
        Users users = new Users(request.name(), request.balance());
        return users.getId();
    }
}
