package com.example.my_spring_server.jpa;

import java.lang.reflect.Field;

public class MyEntityIdInjector {
    public static void injectId(Object entity, Long id) {
        injectId(entity, "id", id);
    }

    public static void injectId(Object entity, String fieldName, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new IllegalStateException("id 주입 실패", e);
        }
    }
}
