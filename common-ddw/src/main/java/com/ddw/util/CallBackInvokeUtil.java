package com.ddw.util;

import com.ddw.beans.ExitOrderPO;
import com.ddw.beans.ReviewCallBackBean;

import java.lang.reflect.Method;

public class CallBackInvokeUtil {
    public  static Object reviewInvoke(Object obj,String methodName,ReviewCallBackBean rb)throws Exception{
        Class clz=obj.getClass();
        Method method=clz.getMethod(methodName,ReviewCallBackBean.class);
        return method.invoke(obj,rb);
    }
    public  static Object exitOrderInvoke(Object obj,String methodName,ExitOrderPO rb)throws Exception{
        Class clz=obj.getClass();
        Method method=clz.getMethod(methodName,ExitOrderPO.class);
        return method.invoke(obj,rb);
    }
}
