package com.example.my_spring_server.my.jdbctemplate;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MyJdbcTemplate {
    public int update(Connection conn, String sql, Object... params) {
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgsInPreparedStatement(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(Connection conn, String sql, MyRowMapper<T> mapper, Object... params) {
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgsInPreparedStatement(ps, params);

            List<T> result = new LinkedList<>();
            try(ResultSet rs = ps.executeQuery()) {
                boolean hasData = rs.next();
                if(hasData)
                    return mapper.mapRow(rs);
                else
                    throw new EmptyResultException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(Connection conn, String sql, MyRowMapper<T> mapper, Object... params) {
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgsInPreparedStatement(ps, params);

            List<T> result = new LinkedList<>();
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    result.add(mapper.mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setArgsInPreparedStatement(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++)
            ps.setObject(i + 1, params[i]);
    }

}
