package com.gen.common.util;

public class ThreadLocalUtil {
    private static ThreadLocal threadLocal=new ThreadLocal();
    public static void set(Object obj){
        threadLocal.set(obj);
    }
    public static Object get(){
        Object obj=threadLocal.get();
        threadLocal.remove();
        return obj;
    }
}
