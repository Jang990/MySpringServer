package com.example.my_spring_server.foods.infra;

import com.example.my_spring_server.foods.domain.Foods;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.my.jdbctemplate.EmptyResultException;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.my.jdbctemplate.MyKeyHolder;
import com.example.my_spring_server.my.jdbctemplate.MyRowMapper;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class FoodsRepository {
    private final MyJdbcTemplate myJdbcTemplate;

    private static final MyRowMapper<Foods> foodRowMapper = (rs) -> {
        long foodId = rs.getLong(1);
        Foods result = new Foods(rs.getString(2), rs.getInt(3), rs.getInt(4));
        MyEntityIdInjector.injectId(result, foodId);
        return result;
    };

    public FoodsRepository(MyJdbcTemplate myJdbcTemplate) {
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public Foods save(Foods food) {
        MyKeyHolder myKeyHolder = new MyKeyHolder();
        myJdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
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
    }

    public Foods findById(long id) {
        try {
            return myJdbcTemplate.queryForObject("""
                    SELECT id, name, price, stock
                    FROM foods
                    WHERE id = ?
                    """, foodRowMapper, id);
        } catch(EmptyResultException e) {
            throw new IllegalArgumentException("음식을 찾을 수 없습니다. userId=%d".formatted(id), e);
        }
    }

    public List<Foods> findAll(List<Long> ids) {
        if(ids.isEmpty())
            throw new IllegalArgumentException("음식 검색 시 ids는 필수");

        return myJdbcTemplate.query(createFindAllSql(ids.size()), foodRowMapper, ids.toArray());
    }

    private String createFindAllSql(int idsSize) {
        String idString = String.join(",", Collections.nCopies(idsSize, "?"));
        return """
            SELECT id, name, price, stock
            FROM foods
            WHERE id in ( %s )
            """.formatted(idString);
    }

    public void updateStock(long foodId, int stock) {
        myJdbcTemplate.update("""
                UPDATE foods
                SET stock = ?
                WHERE id = ?
                """, stock, foodId);
    }
}
