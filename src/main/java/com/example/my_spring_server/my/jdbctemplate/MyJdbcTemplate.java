package com.example.my_spring_server.my.jdbctemplate;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyJdbcTemplate {
    public int update(Connection conn, String sql, Object... params) {
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                ps.setObject(i + 1, params[i]);

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
