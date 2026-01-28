package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.jdbctemplate.EmptyResultException;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.my.jdbctemplate.MyKeyHolder;
import com.example.my_spring_server.my.jdbctemplate.MyRowMapper;
import com.example.my_spring_server.users.domain.Users;

import java.sql.*;

public class UsersRepository {
    private static final MyRowMapper<Users> userRowMapper = rs -> {
        long userId = rs.getLong(1);
        Users users = new Users(rs.getString(2), rs.getInt(3));
        MyEntityIdInjector.injectId(users, userId);
        return users;
    };

    private final MyDataSource myDataSource;
    private final MyJdbcTemplate myJdbcTemplate;

    public UsersRepository(MyDataSource myDataSource, MyJdbcTemplate myJdbcTemplate) {
        this.myDataSource = myDataSource;
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public Users save(Users users) {
        try(Connection conn = myDataSource.getConnection()) {
            MyKeyHolder myKeyHolder = new MyKeyHolder();
            myJdbcTemplate.update(
                    conn,
                    con -> {
                        PreparedStatement ps = conn.prepareStatement("""
                                Insert Into Users (name, balance)
                                values
                                (? ,?)
                                """, Statement.RETURN_GENERATED_KEYS); // 플레그 전달 - 생성된 ID 반환
                        ps.setString(1, users.getName());
                        ps.setInt(2, users.getBalance());

                        return ps;
                    }, myKeyHolder
            );

            MyEntityIdInjector.injectId(users, myKeyHolder.getId());
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Users findById(long id) {
        try (Connection conn = myDataSource.getConnection()) {
            try {
                return myJdbcTemplate.queryForObject(conn, """
                            SELECT id, name, balance
                            FROM USERS
                            WHERE id = ?
                            """,
                        userRowMapper, id);
            }  catch(EmptyResultException e) {
                throw new IllegalArgumentException("음식을 찾을 수 없습니다. userId=%d".formatted(id), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBalance(long userId, int balance) {
        try (Connection conn = myDataSource.getConnection()) {
            updateBalance(conn, userId, balance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBalance(Connection conn, long userId, int balance) throws SQLException {
        myJdbcTemplate.update(conn, """
                    UPDATE users
                    SET balance = ?
                    WHERE id = ?
                    """, balance, userId);
    }
}
