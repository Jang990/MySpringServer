package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.users.domain.Users;

import java.sql.*;

public class UsersRepository {
    private final DBConfig dbConfig;

    public UsersRepository(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Users save(Users users) {
        try(Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
            PreparedStatement ps = conn.prepareStatement("""
                    Insert Into Users (name, balance)
                    values
                    (? ,?)
                    """, Statement.RETURN_GENERATED_KEYS); // 플레그 전달 - 생성된 ID 반환
        ) {
            ps.setString(1, users.getName());
            ps.setInt(2, users.getBalance());
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()) {
                boolean hasId = rs.next();
                long id = rs.getLong(1); // id 반환
                MyEntityIdInjector.injectId(users, id);
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Users findById(long id) {
        try (
                Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
                PreparedStatement ps = conn.prepareStatement("""
                        SELECT id, name, balance
                        FROM USERS
                        WHERE id = ?
                        """)
        ) {
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                boolean hasUser = rs.next();
                if(!hasUser)
                    throw new IllegalArgumentException("User를 찾을 수 없습니다. userId={}".formatted(id));
                long userId = rs.getLong(1);
                Users users = new Users(rs.getString(2), rs.getInt(3));
                MyEntityIdInjector.injectId(users, userId);
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBalance(long userId, int balance) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            updateBalance(conn, userId, balance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBalance(Connection conn, long userId, int balance) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
                    UPDATE users
                    SET balance = ?
                    WHERE id = ?
                    """)) {
            ps.setInt(1, balance);
            ps.setLong(2, userId);

            ps.executeUpdate();
        }
    }
}
