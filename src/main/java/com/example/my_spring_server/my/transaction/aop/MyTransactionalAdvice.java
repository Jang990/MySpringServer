package com.example.my_spring_server.my.transaction.aop;

import com.example.my_spring_server.my.transaction.MyTransactionManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyTransactionalAdvice implements MethodInterceptor {
    private final MyTransactionManager myTransactionManager;

    public MyTransactionalAdvice(MyTransactionManager myTransactionManager) {
        this.myTransactionManager = myTransactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            myTransactionManager.startTransaction();
            Object result = invocation.proceed();
            myTransactionManager.commit();

            return result;
        } catch(Throwable throwable) {
            myTransactionManager.rollback();
            throw throwable;
        }
    }
}
