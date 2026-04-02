package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


public class _3_CGLIBTest {
    @Test
    @DisplayName("CGLIB - 인터페이스가 없는 클래스")
    void sample1() throws Exception {
        OnlyClass onlyClass = new OnlyClass();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(OnlyClass.class); // 해당 클래스를 상속받아 프록시를 생성할 것임
        enhancer.setCallback(new StringTransactionInterceptor(onlyClass)); // 프록시 실행 로직
        OnlyClass proxy = (OnlyClass) enhancer.create(); // 생성

        String result = proxy.service("프록시");
        System.out.println(result);

        System.out.println("\nonlyClass 클래스=" + onlyClass.getClass()); // class com...OnlyClass
        System.out.println("Proxy 클래스=" + proxy.getClass()); // class com...OnlyClass$$EnhancerByCGLIB$$8402e104
    }


    // import org.springframework.cglib.proxy.MethodInterceptor;
    static class StringTransactionInterceptor implements MethodInterceptor {

        private final Object target;
        public StringTransactionInterceptor(Object target) { this.target = target; }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println();
            System.out.println("CGLIB 트랜잭션 시작!");

            // Object result = method.invoke(target, args); <= 이렇게 할 수 있지만
            // MethodProxy를 쓰는게 내부적으로 최적화가 돼있어 조금 더 빠르다.
            Object result = proxy.invoke(target, args);

            System.out.println("CGLIB 트랜잭션 종료! => " + result);
            return result;
        }
    }
}
