package com.example.my_spring_server.orders.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.my.jdbctemplate.MyJdbcTemplate;
import com.example.my_spring_server.my.jdbctemplate.MyKeyHolder;
import com.example.my_spring_server.my.jdbctemplate.MyRowMapper;
import com.example.my_spring_server.orders.domain.OrderItems;
import com.example.my_spring_server.orders.domain.Orders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class OrderRepository {
    private static final MyRowMapper<Orders> ordersMyRowMapper = rs -> {
        Long orderId = null;
        long userId = -1;
        int totalPrice = -1;
        LocalDateTime createdAt = null;
        List<OrderItems> orderItems = new LinkedList<>();

        while (true) {
            if (orderId == null) {
                orderId = rs.getLong(1);
                userId = rs.getLong(2);
                totalPrice = rs.getInt(3);
                createdAt = rs.getObject(4, LocalDateTime.class);
            }

            long orderItemId = rs.getLong(5);
            long foodId = rs.getLong(6);
            int priceAtOrder = rs.getInt(7);
            int quantity = rs.getInt(8);

            // 기본 생성자로 생성
            OrderItems orderItem = createOrderItemWithReflection(orderItemId, orderId, foodId, priceAtOrder, quantity);
            orderItems.add(orderItem);

            if(!rs.next())
                break;
        }

        return createOrderWithReflection(orderId, userId, totalPrice, createdAt, orderItems);
    };

    private final DBConfig dbConfig;
    private final MyJdbcTemplate myJdbcTemplate;

    public OrderRepository(DBConfig dbConfig, MyJdbcTemplate myJdbcTemplate) {
        this.dbConfig = dbConfig;
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public Orders save(Orders order) {
        try(Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) { // Order 삽입 ps
            return save(conn, order);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Orders save(Connection conn, Orders order) throws SQLException {
        try {
            conn.setAutoCommit(false);

            insertOrder(conn, order);
            insertOrderItems(conn, order.getOrderItems(), order.getId());
            
            conn.commit();
            return order;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public Orders findById(long id) {
        try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            return myJdbcTemplate.queryForObject(conn, """
                    SELECT o.id, o.user_id, o.total_price, o.created_at, oi.id, oi.food_id, oi.price_at_order, oi.quantity
                    FROM orders o
                    LEFT OUTER JOIN order_items oi ON o.id = oi.order_id
                    WHERE o.id = ?
                    """, ordersMyRowMapper, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Orders createOrderWithReflection(Long orderId, long userId, int totalPrice, LocalDateTime createdAt, List<OrderItems> orderItems) {
        try {
            Constructor<Orders> defaultConstructor = Orders.class.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            Orders order = defaultConstructor.newInstance();

            MyEntityIdInjector.injectId(order, "id", orderId);
            MyEntityIdInjector.injectId(order, "userId", userId);
            MyEntityIdInjector.injectId(order, "totalPrice", totalPrice);
            MyEntityIdInjector.injectId(order, "createdAt", createdAt);
            MyEntityIdInjector.injectId(order, "orderItems", orderItems);
            return order;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static OrderItems createOrderItemWithReflection(long orderItemId, Long orderId, long foodId, int priceAtOrder, int quantity) {
        try {
            Constructor<OrderItems> defaultConstructor = OrderItems.class.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            OrderItems orderItem = defaultConstructor.newInstance();

            MyEntityIdInjector.injectId(orderItem, "id", orderItemId);
            MyEntityIdInjector.injectId(orderItem, "orderId", orderId);
            MyEntityIdInjector.injectId(orderItem, "foodId", foodId);
            MyEntityIdInjector.injectId(orderItem, "priceAtOrder", priceAtOrder);
            MyEntityIdInjector.injectId(orderItem, "quantity", quantity);
            return orderItem;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // Order 삽입
    private void insertOrder(Connection conn, Orders order) throws SQLException {
        MyKeyHolder orderKeyHolder = new MyKeyHolder();
        myJdbcTemplate.update(
                conn,
                con -> {
                    PreparedStatement ps = conn.prepareStatement("""
                            INSERT INTO orders (user_id, total_price, created_at)
                            VALUES
                            (?, ?, ?)
                            """, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, order.getUserId());
                    ps.setInt(2, order.getTotalPrice());
                    ps.setObject(3, order.getCreatedAt());
                    return ps;
                }, orderKeyHolder);

        MyEntityIdInjector.injectId(order, orderKeyHolder.getId());
    }

    // OrderItems 삽입
    private void insertOrderItems(Connection conn, List<OrderItems> orderItems, long orderId) throws SQLException {
        for (OrderItems orderItem : orderItems) {
            MyKeyHolder orderItemKeyHolder = new MyKeyHolder();
            myJdbcTemplate.update(
                    conn,
                    con -> {
                        PreparedStatement psOrderItems = conn.prepareStatement("""
                                    INSERT INTO order_items (order_id, food_id, price_at_order, quantity)
                                    VALUES
                                    (?, ?, ?, ?)
                                    """, Statement.RETURN_GENERATED_KEYS);
                        psOrderItems.setLong(1, orderId);
                        psOrderItems.setLong(2, orderItem.getFoodId());
                        psOrderItems.setInt(3, orderItem.getPriceAtOrder());
                        psOrderItems.setInt(4, orderItem.getQuantity());

                        return psOrderItems;
                    },
                    orderItemKeyHolder
            );

            MyEntityIdInjector.injectId(orderItem, orderItemKeyHolder.getId());
            MyEntityIdInjector.injectId(orderItem, "orderId", orderId);
        }
    }

}
