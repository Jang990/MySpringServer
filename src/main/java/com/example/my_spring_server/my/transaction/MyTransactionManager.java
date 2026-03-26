package com.example.my_spring_server.my.transaction;

import com.example.my_spring_server.my.datasource.MyDataSource;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MyTransactionManager {
    private final MyDataSource myDataSource;

    public MyTransactionManager(
            MyDataSource myTransactionAwareDataSourceProxy
    ) {
        this.myDataSource = myTransactionAwareDataSourceProxy;
    }

    public void startTransaction() {
        if(MyConnectionThreadLocal.getConnection() != null)
            throw new IllegalStateException("이미 진행중인 트랜잭션이 있음");

        try {
            Connection conn = myDataSource.getConnection();
            conn.setAutoCommit(false);
            MyConnectionThreadLocal.bindConnection(conn);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 시작 예외", e);
        }
    }

    public void commit() {
        Connection conn = MyConnectionThreadLocal.getConnection();
        if(conn == null)
            return;

        try(conn) {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 커밋 예외",e);
        } finally {
            MyConnectionThreadLocal.clear();
        }
    }

    public void rollback() {
        Connection conn = MyConnectionThreadLocal.getConnection();
        if(conn == null)
            return;

        try(conn) {
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("트랜잭션 롤백 예외", e);
        } finally {
            MyConnectionThreadLocal.clear();
        }
    }
}
