package com.example.my_spring_server.users.application;

import com.example.my_spring_server.users.domain.Users;
import com.example.my_spring_server.users.infra.UsersRepository;
import com.example.my_spring_server.users.presentation.dto.UserCreationRequest;

public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Long createUser(UserCreationRequest request) {
        Users users = new Users(request.name(), request.balance());
        return usersRepository.save(users).getId();
    }
}
