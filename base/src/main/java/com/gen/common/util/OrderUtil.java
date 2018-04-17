package com.gen.common.util;

import com.gen.common.exception.GenException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class OrderUtil {
    public static String createOrderNo(String orderDate,Integer orderType,Integer payType,Integer id){
        StringBuilder orderNo=new StringBuilder();
        orderNo.append("01");//表示01库，以后可能会分库
        orderNo.append(orderDate);//时间
        orderNo.append("0").append(orderType);//订单类型
        orderNo.append("0").append(payType);//支付类型
        int length=32-(orderNo.length()+id.toString().length());
        for(int i=0;i<length;i++){
            orderNo.append("0");
        }
        orderNo.append(id);//订单id号

        return orderNo.toString();
    }
    public static String getOrderTime(String orderNo)throws GenException{
        if(orderNo.length()!=32|| !orderNo.matches("^0120[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}0[0-9]{1}0[0-9]{1}[0-9]+$")){
            throw new GenException("订单号格式有错");
        }
       return orderNo.substring(2,16);
    }
    public static Integer getOrderId(String orderNo)throws GenException{
        if(orderNo.length()!=32|| !orderNo.matches("^0120[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}0[0-9]{1}0[0-9]{1}[0-9]+$")){
            throw new GenException("订单号格式有错");
        }
        return Integer.parseInt(orderNo.substring(20,orderNo.length()).replaceFirst("^0+(.*)$","$1"));
    }

    public static void main(String[] args)throws Exception {
        System.out.println(getOrderId(createOrderNo(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"),1,1,1000000000)));
    }
}
