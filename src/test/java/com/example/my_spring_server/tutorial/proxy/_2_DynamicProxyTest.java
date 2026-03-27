package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.AAAImpl;
import com.example.my_spring_server.tutorial.proxy.sample.AAAInter;
import com.example.my_spring_server.tutorial.proxy.sample.BBBImpl;
import com.example.my_spring_server.tutorial.proxy.sample.BBBInter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 동적 프록시 활용
public class _2_DynamicProxyTest {

    @Test
    @DisplayName("AAA - 트랜잭션 부가 기능")
    void sample1() throws Exception {
        AAAInter AAA = new AAAImpl();
        StringTransactionHandler handler = new StringTransactionHandler(AAA);

        AAAInter AAAProxy = (AAAInter) Proxy.newProxyInstance( // 동적 프록시 생성
                AAAInter.class.getClassLoader(),
                new Class[]{AAAInter.class}, // 어떤 인터페이스를 기반으로 프록시를 만들지
                handler // 프록시에 사용할 로직
        );

        AAAProxy.helloA("동적_프록시");

        System.out.println("\nAAA 클래스=" + AAA.getClass()); // class com...AAAImpl
        System.out.println("AAAProxy 클래스=" + AAAProxy.getClass()); // class jdk.proxy3.$Proxy13
    }

    @Test
    @DisplayName("BBB - 트랜잭션 부가 기능")
    void sample2() throws Exception {
        BBBInter BBB = new BBBImpl();
        StringTransactionHandler handler = new StringTransactionHandler(BBB);

        BBBInter BBBProxy = (BBBInter) Proxy.newProxyInstance(
                BBBInter.class.getClassLoader(),
                new Class[]{BBBInter.class},
                handler
        );

        BBBProxy.printB();
    }

    static class StringTransactionHandler implements InvocationHandler {
        private final Object target;

        public StringTransactionHandler(Object target) { this.target = target; }

        @Override
        public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
            System.out.println();
            System.out.println("트랜잭션 시작!");

            Object result = method.invoke(target, objects);

            System.out.println("트랜잭션 종료! => " + result);
            return result;
        }
    }

}
