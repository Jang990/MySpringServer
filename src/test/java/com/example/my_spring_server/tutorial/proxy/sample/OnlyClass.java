package com.example.my_spring_server.tutorial.proxy.sample;

public class OnlyClass {
    public String service(String prefix) {
        System.out.println(prefix + "_인터페이스가 없어요!");
        return "OnlyClass";
    }

    public void hello() {
        System.out.println("안녕하세요.");
    }
}
