package com.example.my_spring_server.my.transaction.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

//@Component
@Deprecated
public class MyTransactionalBeanPostProcessor implements BeanPostProcessor {
    private final MyTransactionalAnnotationChecker myTransactionalAnnotationChecker;
    private final MyTransactionalAdvice myTransactionalAdvice;
    private final Pointcut myTransactionalPointcut;

    public MyTransactionalBeanPostProcessor(
            MyTransactionalAnnotationChecker myTransactionalAnnotationChecker,
            MyTransactionalAdvice myTransactionalAdvice,
            Pointcut myTransactionalPointcut
    ) {
        this.myTransactionalAnnotationChecker = myTransactionalAnnotationChecker;
        this.myTransactionalAdvice = myTransactionalAdvice;
        this.myTransactionalPointcut = myTransactionalPointcut;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /*
        Advisor에서 어노테이션 여부를 체크하는데 모든 빈을 프록시로 감싸면 되지 않는가?
        1. 컨테이너 내의 모든 빈을 프록시로 감싸는 작업때문에 시작시간이 느려진다. - CGLIB는 새로운 클래스(바이트코드)를 동적으로 생성으로 무거운 작업이다.
        2. 불필요한 프록시 로직 추가로 메모리 공간 낭비
         */
        if(!myTransactionalAnnotationChecker.hasAnnotation(bean))
            return bean;

        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvisor(
                new DefaultPointcutAdvisor(
                        myTransactionalPointcut,
                        myTransactionalAdvice
                )
        );
        return proxyFactory.getProxy();
    }

}