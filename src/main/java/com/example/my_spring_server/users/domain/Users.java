package com.example.my_spring_server.users.domain;

public class Users {
    private Long id;
    private String name;
    private int balance;

    protected Users() {}

    public Users(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }
}
