package com.example.my_spring_server;

public class MySQLConfig implements DBConfig {
    @Override
    public String getUrl() {
        return "jdbc:mysql://localhost:3306/spring_server_study";
    }

    @Override
    public String getUsername() {
        return "root";
    }

    @Override
    public String getPassword() {
        return "1234";
    }
}
