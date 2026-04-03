package com.example.my_spring_server.my.transaction.aop;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class MyTransactionalAnnotationChecker {

    private static final Class<MyTransactional> ANNOTATION_TYPE = MyTransactional.class;

    public boolean hasAnnotation(Object obj) {
        if(obj == null)
            return false;

        return isEligibleClass(obj) || hasEligibleMethod(obj);
    }

    private boolean hasEligibleMethod(Object obj) {
        for (Method method : AopUtils.getTargetClass(obj).getMethods()) {
            if(hasAnnotation(method))
                return true;
        }
        return false;
    }

    private boolean isEligibleClass(Object obj) {
        return hasAnnotation(
                AopUtils.getTargetClass(obj)
        );
    }

    private boolean hasAnnotation(Class<?> targetClass) {
        return AnnotationUtils.findAnnotation(targetClass, ANNOTATION_TYPE) != null;
    }

    private boolean hasAnnotation(Method method) {
        return AnnotationUtils.findAnnotation(method, ANNOTATION_TYPE) != null;
    }
}
