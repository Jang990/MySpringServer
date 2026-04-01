package com.example.my_spring_server.tutorial.postprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

public class _1_BasicTest {
    @Test
    void test() {
        ApplicationContext ac =
                new AnnotationConfigApplicationContext(BasicConfig.class);

        A beanA = ac.getBean("beanA", A.class);
        beanA.helloA();

        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean(B.class));

        // hello A
    }

    static class BasicConfig {
        @Bean("beanA")
        public A a() {
            return new A();
        }
    }

    static class A {
        public void helloA() {
            System.out.println("hello A");
        }
    }

    static class B {
        public void helloB() {
            System.out.println("hello B");
        }
    }
}
