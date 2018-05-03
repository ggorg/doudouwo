package com.ddw.util;

import com.ddw.beans.ReviewCallBackBean;

import java.lang.reflect.Method;

public class ReviewCallBackUtil {
    public  static Object invoke(Object obj,String methodName,ReviewCallBackBean rb)throws Exception{
        Class clz=obj.getClass();
        Method method=clz.getMethod(methodName,ReviewCallBackBean.class);
        return method.invoke(obj,rb);
    }
}
