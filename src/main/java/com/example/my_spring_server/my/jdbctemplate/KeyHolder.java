package com.example.my_spring_server.my.jdbctemplate;

public class KeyHolder {
    private Long id;

    protected void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
