package com.example.my_spring_server.tutorial.proxy.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 코드 중복 문제
public class SampleProblem {
    @Test
    @DisplayName("AAA - 트랜잭션 부가 기능")
    void sample1() {
        AAAInter AAA = new AAAImpl();

        System.out.println("트랜잭션 시작!");
        String result = AAA.helloA();
        System.out.println("트랜잭션 종료! => " + result);
    }

    @Test
    @DisplayName("BBB - 트랜잭션 부가 기능")
    void sample2() {
        BBBInter BBB = new BBBImpl();

        System.out.println("트랜잭션 시작!");
        String result = BBB.printB();
        System.out.println("트랜잭션 종료! => " + result);
    }
}
