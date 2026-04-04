package com.example.my_spring_server.my.transaction.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyTransactionalPointcutConfig {
    @Bean
    public Pointcut myTransactionalPointcut() {
        // ComposablePointcut composablePointcut = new  ComposablePointcut(); -> 이렇게 쓰면 안됨

        // classPointcut처럼 두 번째 파라미터가 true면 상속받은 클래스나 인터페이스의 어노테이션도 찾는다
        Pointcut classPointcut = new AnnotationMatchingPointcut(MyTransactional.class, true); // 클래스
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, MyTransactional.class); // 메소드
        return new ComposablePointcut(classPointcut).union(methodPointcut); // 클래스 OR 메소드
    }
}
