package com.example.my_spring_server.my.datasource;

import com.example.my_spring_server.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerDataSource implements MyDataSource {
    private final DBConfig dbConfig;

    public DriverManagerDataSource(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }
}
