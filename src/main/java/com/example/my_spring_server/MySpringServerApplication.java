package com.example.my_spring_server;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//@SpringBootApplication
public class MySpringServerApplication {

	public static void main(String[] args) {
		/*SpringApplication.run(MySpringServerApplication.class, args);*/
		ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		System.out.println("Hello World");
	}

}
