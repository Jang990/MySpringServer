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

    public Foods findById(long id) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT id, name, price, stock
                     FROM foods
                     WHERE id = ?
                     """)) {
            ps.setLong(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                boolean hasFood = rs.next();
                if(!hasFood)
                    throw new IllegalArgumentException("음식을 찾을 수 없습니다. userId=%d".formatted(id));

                long foodId = rs.getLong(1);
                Foods result = new Foods(rs.getString(2), rs.getInt(3), rs.getInt(4));
                MyEntityIdInjector.injectId(result, foodId);
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
