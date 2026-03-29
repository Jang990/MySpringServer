package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.OnlyClass;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

public class _5_AdvisorTest {
    @Test
    @DisplayName("어드바이저 적용 - 항상 적용 포인트컷 + String 트랜잭션 어드바이스")
    void sample1() throws Exception {
        OnlyClass onlyClass = new OnlyClass();
        ProxyFactory proxyFactory = new ProxyFactory(onlyClass);

        // proxyFactory.addAdvice(new StringTransactionAdvice()); 내부 코드를 보면 아래 코드와 똑같다.
        proxyFactory.addAdvisor(
                new DefaultPointcutAdvisor(
                        Pointcut.TRUE, // 항상 적용한다.
                        new StringTransactionAdvice()
                )
        );

        OnlyClass proxy = (OnlyClass) proxyFactory.getProxy();
        AspectJExpressionPointcut
        proxy.service("OnlyClass");
        proxy.hello();
    }

    @Test
    @DisplayName("포인트컷 변경 - hello 메소드에만 프록시 로직 적용")
    void sample2() throws Exception {
        OnlyClass onlyClass = new OnlyClass();
        ProxyFactory proxyFactory = new ProxyFactory(onlyClass);

        proxyFactory.addAdvisor(
                new DefaultPointcutAdvisor(
                        new MyPointcut(), // 메소드명이 hello일 경우에만 true를 반환한다.
                        new StringTransactionAdvice()
                )
        );

        OnlyClass proxy = (OnlyClass) proxyFactory.getProxy();

        proxy.service("OnlyClass");
        proxy.hello();
    }

    /*
    직접 구현할 일은 별로 없다. Spring에서 제공하는게 있다. 하지만 학습용으로 한 번 만 구현한다
    포인트 컷은 ClassFilter, MethodFilter가 둘 다 true를 반환해야한다.
    */
    static class MyPointcut implements Pointcut {

        // 클래스 조건으로 필터링
        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE; // 항상 true를 반환하는 ClassFilter
        }

        // 메소드 조건으로 필터링
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }

        // 메소드명이 hello일 경우에만 true를 반환한다.
        static class MyMethodMatcher implements MethodMatcher {

            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                boolean result = method.getName().equals("hello");
                return result;
            }


            @Override
            public boolean isRuntime() { return false; }
            // isRuntime이 True라면 아래 matches가 호출된다. - 참고만 해라

            // 신경 X
            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) { return false; }
        }
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


    @Test
    @DisplayName("스프링 제공 포인트 컷 - hello 메소드에만 프록시 로직 적용")
    void sample3() throws Exception {
        OnlyClass onlyClass = new OnlyClass();
        ProxyFactory proxyFactory = new ProxyFactory(onlyClass);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("hello");
        proxyFactory.addAdvisor(
                new DefaultPointcutAdvisor(
                        pointcut,
                        new StringTransactionAdvice()
                )
        );


        OnlyClass proxy = (OnlyClass) proxyFactory.getProxy();

        proxy.service("OnlyClass");
        proxy.hello();
    }
}
