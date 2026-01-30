package com.example.my_spring_server.my.datasource.transaction;

import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.transaction.MyConnectionThreadLocal;

import java.sql.Connection;
import java.sql.SQLException;

public class MyTransactionAwareDataSourceProxy implements MyDataSource {
    private final MyDataSource delegate;

    public MyTransactionAwareDataSourceProxy(MyDataSource myDataSource) {
        this.delegate = myDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection txConn = MyConnectionThreadLocal.getConnection();
        if(txConn == null)
            return delegate.getConnection();
        else
            return txConn;
    }
}