package com.example.my_spring_server.users.domain;

public class Users {
    private Long id;
    private String name;
    private int balance;

    public Users(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }
}
