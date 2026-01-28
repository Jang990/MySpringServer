package com.example.my_spring_server.my.jdbctemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface MyPreparedStatementCreator {
    PreparedStatement createPreparedStatement(Connection con) throws SQLException;
}
