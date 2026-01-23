package com.example.my_spring_server.orders.infra;

import com.example.my_spring_server.DBConfig;
import com.example.my_spring_server.jpa.MyEntityIdInjector;
import com.example.my_spring_server.orders.domain.OrderItems;
import com.example.my_spring_server.orders.domain.Orders;

import java.sql.*;
import java.util.List;

public class OrderRepository {
    private final DBConfig dbConfig;

    public OrderRepository(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Orders save(Orders order) {
        try(Connection conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) { // Order 삽입 ps
            conn.setAutoCommit(false);

            try {
                insertOrder(conn, order);
                insertOrderItems(conn, order.getOrderItems(), order.getId());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

            return order;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Order 삽입
    private void insertOrder(Connection conn, Orders order) throws SQLException {
        try(PreparedStatement psOrder = conn.prepareStatement("""
                INSERT INTO orders (user_id, total_price, created_at)
                VALUES
                (?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            psOrder.setLong(1, order.getUserId());
            psOrder.setInt(2, order.getTotalPrice());
            psOrder.setObject(3, order.getCreatedAt());

            psOrder.executeUpdate();

            try (ResultSet orderIdResultSet = psOrder.getGeneratedKeys()) { // Order 삽입으로 생성된 키 확인
                orderIdResultSet.next();
                long orderId = orderIdResultSet.getLong(1);
                MyEntityIdInjector.injectId(order, orderId);
            }
        }
    }

    // OrderItems 삽입
    private void insertOrderItems(Connection conn, List<OrderItems> orderItems, long orderId) throws SQLException {
        try (PreparedStatement psOrderItems = conn.prepareStatement("""
                INSERT INTO order_items (order_id, food_id, price_at_order, quantity)
                VALUES
                (?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            for (OrderItems orderItem : orderItems) {
                psOrderItems.setLong(1, orderId);
                psOrderItems.setLong(2, orderItem.getFoodId());
                psOrderItems.setInt(3, orderItem.getPriceAtOrder());
                psOrderItems.setInt(4, orderItem.getQuantity());

                psOrderItems.executeUpdate();

                try (ResultSet orderItemIdResultSet = psOrderItems.getGeneratedKeys()) { // OrderItems의 id, foodId 세팅
                    orderItemIdResultSet.next();
                    long orderItemId = orderItemIdResultSet.getLong(1);
                    MyEntityIdInjector.injectId(orderItem, orderItemId);
                    MyEntityIdInjector.injectId(orderItem, "orderId", orderId);
                }
            }
        }
    }

}
