package com.example.my_spring_server.foods.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.my.jdbctemplate.EmptyResultException;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.my.jdbctemplate.MyKeyHolder;
import com.example.my_spring_server.my.jdbctemplate.MyRowMapper;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FoodsRepository {
    private final DBConfig dbConfig;
    private final MyJdbcTemplate myJdbcTemplate;

    private static final MyRowMapper<Foods> foodRowMapper = (rs) -> {
        long foodId = rs.getLong(1);
        Foods result = new Foods(rs.getString(2), rs.getInt(3), rs.getInt(4));
        MyEntityIdInjector.injectId(result, foodId);
        return result;
    };

    public FoodsRepository(DBConfig dbConfig, MyJdbcTemplate myJdbcTemplate) {
        this.dbConfig = dbConfig;
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public Foods save(Foods food) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            MyKeyHolder myKeyHolder = new MyKeyHolder();
            myJdbcTemplate.update(conn, con -> {
                PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO foods(name, price, stock)
                        VALUES
                        (?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, food.getName());
                ps.setInt(2, food.getPrice());
                ps.setInt(3, food.getStock());
                return ps;
            }, myKeyHolder);

            MyEntityIdInjector.injectId(food, myKeyHolder.getId());
            return food;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Foods findById(long id) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            try {
                return myJdbcTemplate.queryForObject(conn, """
                    SELECT id, name, price, stock
                    FROM foods
                    WHERE id = ?
                    """, foodRowMapper, id);
            } catch(EmptyResultException e) {
                throw new IllegalArgumentException("음식을 찾을 수 없습니다. userId=%d".formatted(id), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Foods> findAll(List<Long> ids) {
        if(ids.isEmpty())
            throw new IllegalArgumentException("음식 검색 시 ids는 필수");

        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            return myJdbcTemplate.query(conn, createFindAllSql(ids.size()), foodRowMapper, ids.toArray());
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
        myJdbcTemplate.update(conn, """
                UPDATE foods
                SET stock = ?
                WHERE id = ?
                """, stock, foodId);
    }
}
