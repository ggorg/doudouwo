package com.gen.common.services;

import com.gen.common.annotation.DBVersion;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//@Component
//@Aspect
public class VersionService  {

    @Pointcut("@annotation(com.gen.common.annotation.DBVersion)")
    public void init(){}

    @Before("init()")
    public void before(JoinPoint point) {
        Object obj=point.getTarget();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method m = obj.getClass().getMethod(point.getSignature().getName(), parameterTypes);
            if (m != null && m.isAnnotationPresent(DBVersion.class)) {
                DBVersion dbVersion = m.getAnnotation(DBVersion.class);
                dbVersion.tableName();
                System.out.println(point.getSignature().getName()+","+point.getArgs().length+","+point.getTarget()+","+point.getThis());
                Object[] oo=point.getArgs();
                oo[1]=2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
