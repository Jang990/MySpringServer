package com.example.my_spring_server;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.my_spring_server")
public class AppConfig {
    // @EnableTransactionManagement, @EnableAspectJAutoProxy 등에 자동 포함되나 내 프로젝트는 그렇지 않으니 수동 등록
    @Bean
    public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }
}
