package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

public class _4_ProxyFactoryTest {
    @Test
    @DisplayName("ProxyFactory - 인터페이스 X")
    void sample1() throws Exception {
        OnlyClass onlyClass = new OnlyClass();
        ProxyFactory proxyFactory = new ProxyFactory(onlyClass);

        proxyFactory.addAdvice(new StringTransactionAdvice());
        OnlyClass proxy = (OnlyClass) proxyFactory.getProxy();
        proxy.service("OnlyClass");
        proxyFactory.setProxyTargetClass(true);

        System.out.println("인터페이스가 X="+ proxy.getClass()); // class com.example...OnlyClass$$SpringCGLIB$$0
    }

    @Test
    @DisplayName("ProxyFactory - 인터페이스 O")
    void sample2() throws Exception {
        BBBInter BBB = new BBBImpl();
        ProxyFactory proxyFactory = new ProxyFactory(BBB);

        proxyFactory.addAdvice(new StringTransactionAdvice());
        BBBInter proxy = (BBBInter) proxyFactory.getProxy();
        proxy.printB();

        System.out.println("인터페이스 O="+ proxy.getClass());
    }

    // import org.aopalliance.intercept.MethodInterceptor - CGLIB에서 쓴 것과는 다르다
    // MethodInterceptor > Interceptor >  Advice
    static class StringTransactionAdvice implements MethodInterceptor {
        // 이제 필요없다.
        // private final Object target;

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println();
            System.out.println("CGLIB 트랜잭션 시작!");

            // 프록시 생성 과정에서 target을 받기 때문에 invocation.proceed를 호출하면 끝
            Object result = invocation.proceed();

            System.out.println("CGLIB 트랜잭션 종료! => " + result);
            return result;
        }
    }

}
