package com.example.my_spring_server.my.datasource.transaction;

import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.transaction.MyConnectionThreadLocal;

import java.lang.reflect.Proxy;
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
            return suppressClose(txConn);
    }

    // Connection의 conn.close()가 동작하지 않도록 프록시 객체를 반환
    private Connection suppressClose(Connection conn) {
        // Proxy.newProxyInstance(loader, interfaces, handler) = 인터페이스 구현 프록시 객체를 생성
        return (Connection) Proxy.newProxyInstance(
                conn.getClass().getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    // close 메소드 호출이 오면 아무 동작도 하지 않음
                    if ("close".equals(method.getName()))
                        return null;
                    return method.invoke(conn, args); // 다른 메소드들은 conn 메소드로 정상 동작
                }
        );
    }
}