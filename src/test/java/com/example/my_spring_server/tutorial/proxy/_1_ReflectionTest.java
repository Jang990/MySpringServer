package com.example.my_spring_server.tutorial.proxy;

import com.example.my_spring_server.tutorial.proxy.sample.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

// 리플렉션 활용
public class _1_ReflectionTest {
    @Test
    @DisplayName("AAA - 트랜잭션 부가 기능")
    void sample1() throws Exception {
        AAAInter AAA = new AAAImpl();

        process(
                AAA,
                "com.example.my_spring_server.tutorial.proxy.sample.AAAImpl",
                "helloA"
        );
    }

    @Test
    @DisplayName("BBB - 트랜잭션 부가 기능")
    void sample2() throws Exception {
        BBBInter BBB = new BBBImpl();

        process(
                BBB,
                "com.example.my_spring_server.tutorial.proxy.sample.BBBImpl",
                "printB"
        );
    }

    // 부가기능을 한 곳으로!
    void process(Object target, String className, String methodName) throws Exception {
        System.out.println();
        System.out.println("트랜잭션 시작!");

        Class targetClass = Class.forName(className); // 클래스 메타정보 불러오기
        Method method = targetClass.getMethod(methodName); // 클레스 > 메소드 메타정보 불러오기
        Object result = method.invoke(target); // (메타정보를 기반으로) Object target의 메소드 실행

        System.out.println("트랜잭션 종료! => " + result);
    }
}
