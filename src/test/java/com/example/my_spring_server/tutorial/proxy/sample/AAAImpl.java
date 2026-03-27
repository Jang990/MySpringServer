package com.example.my_spring_server.tutorial.proxy.sample;

public class AAAImpl implements AAAInter {
    public String helloA(String prefix) {
        System.out.println(prefix+"_Hello AAA!");
        return "AAA";
    }
}