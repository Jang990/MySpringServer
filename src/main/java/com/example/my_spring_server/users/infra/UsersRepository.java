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
}
