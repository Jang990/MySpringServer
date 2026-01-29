package com.example.my_spring_server.my.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface MyDataSource {
    Connection getConnection() throws SQLException;
}
