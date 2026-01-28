package com.example.my_spring_server.my.jdbctemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MyRowMapper<T> {
    // rowNum도 있다는데 여기선 안쓰니까 생략
    T mapRow(ResultSet rs/*, int rowNum*/) throws SQLException;
}
