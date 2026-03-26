package com.example.my_spring_server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//@SpringBootTest
class MySpringServerApplicationTests {

	@Test
	@DisplayName("IoC 컨테이너 테스트")
	void contextLoads() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	}

}
