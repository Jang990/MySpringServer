package com.example.my_spring_server.foods.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.jpa.MyEntityIdInjector;

import java.sql.*;

public class FoodsRepository {
    private final DBConfig dbConfig;

    public FoodsRepository(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Foods save(Foods food) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
                PreparedStatement ps = conn.prepareStatement("""
                     INSERT INTO foods(name, price, stock)
                     VALUES
                     (?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, food.getName());
            ps.setInt(2, food.getPrice());
            ps.setInt(3, food.getStock());

            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                long foodId = rs.getLong(1);
                MyEntityIdInjector.injectId(food, foodId);
                return food;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
