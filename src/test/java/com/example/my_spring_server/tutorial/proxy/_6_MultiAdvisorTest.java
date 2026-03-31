package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;


public class _6_MultiAdvisorTest {
    Advisor advisor1;
    Advisor advisor2;


    @BeforeEach
    void setup() {
        advisor1 = new DefaultPointcutAdvisor(new Advice1());
        advisor2 = new DefaultPointcutAdvisor(new Advice2());
    }

    @Test
    @DisplayName("수동 - 멀티 어드바이저 프록시 -> N개의 프록시 필요")
    void test1() {
        // 프록시를 2번 생성해야 함
        // client -> Advisor2 -> Advisor1 -> target
        BBBInter target = new BBBImpl();

        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        proxyFactory1.addAdvisor(advisor1);
        BBBInter proxy1 = (BBBInter) proxyFactory1.getProxy();

        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        proxyFactory2.addAdvisor(advisor2);
        BBBInter proxy2 = (BBBInter) proxyFactory2.getProxy();

        proxy2.printB();
        /*
        Advisor2
        Advisor1
        BBB!
         */
    }

    @Test
    @DisplayName("스프링 방식 - 멀티 어드바이저 프록시 -> 1개의 프록시에서 Advisor를 직접 호출")
    void test2() {
        // client -> [ Advisor2, Advisor1 ] -> target
        BBBInter target = new BBBImpl();

        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        proxyFactory1.addAdvisor(advisor2);
        proxyFactory1.addAdvisor(advisor1);
        BBBInter proxy = (BBBInter) proxyFactory1.getProxy();

        proxy.printB();
        /*
        Advisor2
        Advisor1
        BBB!
         */
    }

    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advisor1");
            return invocation.proceed();
        }
    }

    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advisor2");
            return invocation.proceed();
        }
    }
}
