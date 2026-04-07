package com.example.my_spring_server.my.transaction;

import com.example.my_spring_server.my.MySQLConfig;
import com.example.my_spring_server.my.datasource.DriverManagerDataSource;
import com.example.my_spring_server.my.datasource.MyDataSource;
import com.example.my_spring_server.my.transaction.aop.MyTransactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringJUnitConfig // 내부 config를 바탕으로 컨테이너를 불러옴
public class MyTransactionalProxyTest {
    @Autowired private NonMyTransactionalObject nonMyTransactionalObject;
    @Autowired private ClassMyTransactionalObject classMyTransactionalObject;
    @Autowired private MethodMyTransactionalObject methodMyTransactionalObject;


    @MockitoBean private MyTransactionManager mockTransactionManager;


    @Test
    @DisplayName("어노테이션 X 클래스 - 트랜잭션 적용 안됨")
    public void test1() {
        // when
        nonMyTransactionalObject.hello();

        // given
        assertThat(AopUtils.isAopProxy(nonMyTransactionalObject)).isFalse();
        verify(mockTransactionManager, never()).startTransaction(); // 트랜잭션 X
    }

    @Test
    @DisplayName("클래스 어노테이션 O - 트랜잭션 적용")
    public void test2() {
        // when
        classMyTransactionalObject.hello();

        // then
        assertThat(AopUtils.isAopProxy(classMyTransactionalObject)).isTrue();
        verify(mockTransactionManager, times(1)).startTransaction();
        verify(mockTransactionManager, times(1)).commit();
    }

    @Test
    @DisplayName("클래스 어노테이션 O - 모든 메서드 트랜잭션 적용")
    public void test3() {
        // when - 메서드 2번 호출
        classMyTransactionalObject.hello();
        classMyTransactionalObject.service();

        // then
        assertThat(AopUtils.isAopProxy(classMyTransactionalObject)).isTrue();
        verify(mockTransactionManager, times(2)).startTransaction();
        verify(mockTransactionManager, times(2)).commit();
    }

    @Test
    @DisplayName("클래스 어노테이션 O - 예외 발생 시 롤백")
    public void test4() {
        // when - 메서드 2번 호출
        assertThatThrownBy(() -> classMyTransactionalObject.error());

        // then
        assertThat(AopUtils.isAopProxy(classMyTransactionalObject)).isTrue();
        verify(mockTransactionManager, times(1)).startTransaction();
        verify(mockTransactionManager, times(1)).rollback(); // 롤백 호출
    }

    @Test
    @DisplayName("메소드 어노테이션 O - 적용되지 않은 메소드 호출")
    public void test5() {
        // given
        methodMyTransactionalObject.hello();

        // when
        assertThat(AopUtils.isAopProxy(methodMyTransactionalObject)).isTrue();
        verify(mockTransactionManager, never()).startTransaction();
        verify(mockTransactionManager, never()).commit();
    }

    @Test
    @DisplayName("메소드 어노테이션 O - 적용된 메소드 호출")
    public void test6() {
        // when
        methodMyTransactionalObject.service();

        // then
        assertThat(AopUtils.isAopProxy(methodMyTransactionalObject)).isTrue();
        verify(mockTransactionManager, times(1)).startTransaction();
        verify(mockTransactionManager, times(1)).commit();
    }

    static class NonMyTransactionalObject {
        public void hello() { }
    }

    @MyTransactional
    static class ClassMyTransactionalObject {
        public void hello() { }

        public void service() { }

        public void error() { throw new IllegalArgumentException(); }
    }

    static class MethodMyTransactionalObject {
        public void hello() { }

        @MyTransactional
        public void service() { }
    }

    @Configuration
    @ComponentScan("com.example.my_spring_server.my.transaction")
    static class MyTransactionalProxyTestConfig {
        @Bean
        public NonMyTransactionalObject nonMyTransactionalObject() {
            return new NonMyTransactionalObject();
        }

        @Bean
        public ClassMyTransactionalObject classMyTransactionalObject() {
            return new ClassMyTransactionalObject();
        }

        @Bean
        public MethodMyTransactionalObject methodMyTransactionalObject() {
            return new MethodMyTransactionalObject();
        }

        @Bean
        public MyDataSource myDataSource() {
            return new DriverManagerDataSource(new MySQLConfig());
        }

        @Bean
        public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
            return new DefaultAdvisorAutoProxyCreator();
        }
    }
}
