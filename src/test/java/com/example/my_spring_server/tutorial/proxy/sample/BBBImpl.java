package com.example.my_spring_server.tutorial.proxy.sample;

public class BBBImpl implements BBBInter {
    public String printB() {
        System.out.println("BBB!");
        return "BBB";
    }
}