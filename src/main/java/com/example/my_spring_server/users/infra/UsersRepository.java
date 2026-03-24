package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.jpa.MyEntityIdInjector;
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

    private final MyJdbcTemplate myJdbcTemplate;

    public UsersRepository(MyJdbcTemplate myJdbcTemplate) {
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public Users save(Users users) {
        MyKeyHolder myKeyHolder = new MyKeyHolder();
        myJdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement("""
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

    }

    public Users findById(long id) {
        try {
            return myJdbcTemplate.queryForObject("""
                            SELECT id, name, balance
                            FROM USERS
                            WHERE id = ?
                            """,
                    userRowMapper, id);
        }  catch(EmptyResultException e) {
            throw new IllegalArgumentException("음식을 찾을 수 없습니다. userId=%d".formatted(id), e);
        }
    }

    public void updateBalance(long userId, int balance) {
        myJdbcTemplate.update("""
                    UPDATE users
                    SET balance = ?
                    WHERE id = ?
                    """, balance, userId);
    }
}
