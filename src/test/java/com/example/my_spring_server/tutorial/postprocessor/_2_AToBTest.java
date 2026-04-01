package com.example.my_spring_server.tutorial.postprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class _2_AToBTest {
    @Test
    void test() {
        ApplicationContext ac =
                new AnnotationConfigApplicationContext(BasicConfig.class);

        // B로 바꿔치기 됨
        B beanA = ac.getBean("beanA", B.class);
        beanA.helloB();

        // A는 스프링 Bean으로 등록 조차 되지 않음. 그림 참고
        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean(A.class));

        /*
        AToB 프로세서 - 빈 초기화 콜백 전 : beanName=beanA
        A - 빈 초기화 콜백
        AToB 프로세서 - 빈 초기화 콜백 후 : beanName=beanA
        hello B
         */
    }

    static class BasicConfig {
        @Bean("beanA")
        public A a() {
            return new A();
        }

        // 후처리기 로직을 사용하려면 Bean으로 등록하면 된다.
        @Bean
        public AToBBeanPostProcessor beanAToBBeanPostProcessor() {
            return new AToBBeanPostProcessor();
        }
    }

    static class A implements InitializingBean {

        // 지금 의존성에는 @PostConstruct가 없어서 인터페이스를 직접 사용함.
        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("A - 빈 초기화 콜백");
        }

        public void helloA() {
            System.out.println("hello A");
        }
    }

    static class B {
        public void helloB() {
            System.out.println("hello B");
        }
    }


    static class AToBBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("AToB 프로세서 - 빈 초기화 콜백 전 : beanName=" + beanName);
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            CommonAnnotationBeanPostProcessor
            System.out.println("AToB 프로세서 - 빈 초기화 콜백 후 : beanName=" + beanName);
            if(bean instanceof A)
                return new B(); // beanName 그대로 B 객체로 교체됨
            return bean;
        }
    }
}
