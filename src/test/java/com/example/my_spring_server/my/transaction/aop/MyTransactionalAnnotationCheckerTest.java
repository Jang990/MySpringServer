package com.example.my_spring_server.my.transaction.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class MyTransactionalAnnotationCheckerTest {
    MyTransactionalAnnotationChecker checker = new MyTransactionalAnnotationChecker();

    @DisplayName("@MyTransactional 어노테이션이 붙어있는지 체크")
    @ParameterizedTest
    @MethodSource("args")
    void test(Object target, boolean expected) {
        assertThat(checker.hasAnnotation(target)).isEqualTo(expected);
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new NonMyTransactionalObject(), false),
                Arguments.of(new ClassMyTransactionalObject(), true),
                Arguments.of(new MethodMyTransactionalObject(), true)
        );
    }

    static class NonMyTransactionalObject { }

    @MyTransactional
    static class ClassMyTransactionalObject {
        public void hello() { }
    }


    static class MethodMyTransactionalObject {
        public void hello() { }

        @MyTransactional
        public void service() { }
    }

}