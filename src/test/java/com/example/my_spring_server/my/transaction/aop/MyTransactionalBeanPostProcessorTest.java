package com.example.my_spring_server.my.transaction.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyTransactionalBeanPostProcessorTest {
    @Mock private MyTransactionalAnnotationChecker checker;
    @Mock private MyTransactionalAdvice advice;
    @Mock private Pointcut pointcut;

    @InjectMocks
    private MyTransactionalBeanPostProcessor processor;

    @Test
    @DisplayName("어노테이션이 없는 bean은 원본 bean으로 반환")
    void test1() {
        // given
        when(checker.hasAnnotation(any())).thenReturn(Boolean.FALSE);
        Object original = "Something";

        // when
        Object result = processor.postProcessAfterInitialization(original, "Test Object");

         // then
        assertThat(result).isSameAs(original);
        assertThat(AopUtils.isAopProxy(result)).isFalse();
    }

    @Test
    @DisplayName("어노테이션이 존재하면 프록시 객체 반환")
    public void test2() {
        // given
        when(checker.hasAnnotation(any())).thenReturn(Boolean.TRUE);
        Object original = "Something";

        // when
        Object result = processor.postProcessAfterInitialization(original, "Test Object");

        // then
        assertThat(result).isNotSameAs(original);
        assertThat(AopUtils.isAopProxy(result)).isTrue();
    }

}