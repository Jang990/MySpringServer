package com.example.my_spring_server.my.transaction;

import com.example.my_spring_server.my.datasource.MyDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MyTransactionManager {
    private final MyDataSource myDataSource;
    private final ThreadLocal<Connection> txThreadLocal = new ThreadLocal<>();

    public MyTransactionManager(MyDataSource myDataSource) {
        this.myDataSource = myDataSource;
    }

    public void startTransaction() {
        try {
            Connection conn = myDataSource.getConnection();
            conn.setAutoCommit(false);
            txThreadLocal.set(conn);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 시작 예외", e);
        }
    }

    public void commit() {
        Connection conn = txThreadLocal.get();
        if(conn == null)
            return;

        try(conn) {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 커밋 예외",e);
        } finally {
            txThreadLocal.remove();
        }
    }

    public void rollback() {
        Connection conn = txThreadLocal.get();
        if(conn == null)
            return;

        try(conn) {
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 롤백 예외", e);
        } finally {
            txThreadLocal.remove();
        }
    }
}
