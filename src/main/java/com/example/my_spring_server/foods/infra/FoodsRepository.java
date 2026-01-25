package com.example.my_spring_server.foods.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.jpa.MyEntityIdInjector;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

                return createFood(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Foods> findAll(List<Long> ids) {
        if(ids.isEmpty())
            throw new IllegalArgumentException("음식 검색 시 ids는 필수");

        try (
                Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
                PreparedStatement ps = conn.prepareStatement(createFindAllSql(ids.size()))
        ) {
            for (int i = 0; i < ids.size(); i++)
                ps.setLong(i + 1, ids.get(i));

            try(ResultSet rs = ps.executeQuery()) {
                List<Foods> foods = new LinkedList<>();
                while (rs.next()) {
                    foods.add(createFood(rs));
                }
                return foods;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String createFindAllSql(int idsSize) {
        String idString = String.join(",", Collections.nCopies(idsSize, "?"));
        return """
            SELECT id, name, price, stock
            FROM foods
            WHERE id in ( %s )
            """.formatted(idString);
    }

    private Foods createFood(ResultSet rs) throws SQLException {
        long foodId = rs.getLong(1);
        Foods result = new Foods(rs.getString(2), rs.getInt(3), rs.getInt(4));
        MyEntityIdInjector.injectId(result, foodId);
        return result;
    }

    public void updateStock(long foodId, int stock) {
        try(Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            updateStock(conn, foodId, stock);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStock(Connection conn, long foodId, int stock) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
                    UPDATE foods
                    SET stock = ?
                    WHERE id = ?
                    """)) {
            ps.setInt(1, stock);
            ps.setLong(2, foodId);

            ps.executeUpdate();
        }
    }
}
