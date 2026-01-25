package com.example.my_spring_server.my.jdbctemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MyRowMapper<T> {
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
