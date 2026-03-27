package com.example.my_spring_server.tutorial.proxy.sample;

public class AAAImpl implements AAAInter {
    public String helloA() {
        System.out.println("Hello AAA!");
        return "AAA";
    }
}