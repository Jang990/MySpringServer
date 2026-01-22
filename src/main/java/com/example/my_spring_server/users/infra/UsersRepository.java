package com.example.my_spring_server.users.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.users.domain.Users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsersRepository {
    private final DBConfig dbConfig;

    public UsersRepository(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Users save(Users users) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
            ps = connection.prepareStatement("""
                    Insert Into Users (name, balance)
                    values
                    (? ,?)
                    """);
            ps.setString(1, users.getName());
            ps.setInt(2, users.getBalance());
            boolean result = ps.execute();
            if(!result)
                throw new IllegalStateException("User Insert 실패");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(ps != null)
                    ps.close();
                if(connection != null)
                    connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
