package com.example.my_spring_server.my.transaction;

import java.sql.Connection;

public class MyConnectionThreadLocal {
    private static final ThreadLocal<Connection> HOLDER = new ThreadLocal<>();

    public static void bindConnection(Connection conn) {
        HOLDER.set(conn);
    }

    public static Connection getConnection() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
