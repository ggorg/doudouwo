package com.ddw.util;

import com.ddw.beans.OrderMaterialPO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.PayStatusEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.OrderService;
import com.ddw.servies.ReviewService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Tools;
import freemarker.template.utility.NumberUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Toolsddw extends Tools {
    public static Integer getCurrentUserId(){
        Map usermap=(Map)getSession("user");
        if(usermap!=null){
            return (Integer)usermap.get("id");
        }
        return null;
    }

    /**
     * key=id,
     * key=uName,
     * key=uNickName
     * @return
     */
    public static Map getUserMap(){
        return (Map)getSession("user");

    }
    public static boolean isOverTime(Date orderEndTime){
        return orderEndTime.before(new Date());
    }
    public static String getOrderStatusInfo(Integer payStatus,Integer shipStatus,Date orderEndTime,String orderNo){
        boolean overflag=isOverTime(orderEndTime);
        if(PayStatusEnum.PayStatus0.getCode()==payStatus){
            if(overflag){
                return "订单已超时";
            }else{
                return "未提交订单";
            }
        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus4.getCode()==shipStatus) {
            try {
                ReviewService rs=getBean(ReviewService.class);
                ReviewPO rpo=rs.getReviewByBusinessCode(orderNo);
                if(rpo!=null){
                    return ReviewStatusEnum.getName(rpo.getDrReviewStatus());
                }
            }catch (Exception e){

            }

            return "退还处理中";
        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus6.getCode()==shipStatus){
            return "订单已关闭";
        }else if(payStatus>PayStatusEnum.PayStatus0.getCode() && shipStatus!=ShipStatusEnum.ShipStatus5.getCode()){

            return "进行中";
        } else{
            return "订单完成";
        }

    }
    public static String getBtnMsgByHq(Integer payStatus,Integer shipStatus,Date orderEndTime,String orderNo){
        boolean overflag=isOverTime(orderEndTime);
        StringBuilder btnBuilder=new StringBuilder();

        if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus0.getCode()==shipStatus){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toAccept('"+MyEncryptUtil.encry(orderNo)+"')\">接受订单</button>");

        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus1.getCode()==shipStatus){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','确认发货','/manager/order/to-makesure-sendgoods-by-hq?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">确认发货</button>");

        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && (ShipStatusEnum.ShipStatus3.getCode()==shipStatus || ShipStatusEnum.ShipStatus8.getCode()==shipStatus) && overflag){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toFinish('"+MyEncryptUtil.encry(orderNo)+"')\">完成订单</button>");

        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus4.getCode()==shipStatus){
            try {
                ReviewService rs=getBean(ReviewService.class);
                ReviewPO rpo=rs.getReviewByBusinessCode(orderNo);
                if(rpo.getDrReviewStatus()== ReviewStatusEnum.ReviewStatus0.getCode()){
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','退还受理','/manager/review/to-review-exitback-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">退换受理</button>");

                }else{
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','580px','审批情况','/manager/review/to-review-info-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">审批情况</button>");

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus7.getCode()==shipStatus){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toMakeSureByHq('"+MyEncryptUtil.encry(orderNo)+"')\">确认签收</button>");

        }

        return btnBuilder.toString();
    }
    public static String getBtnMsg(Integer payStatus,Integer shipStatus,Date orderEndTime,String orderNo){
        boolean overflag=isOverTime(orderEndTime);
        StringBuilder btnBuilder=new StringBuilder();
        if(PayStatusEnum.PayStatus0.getCode()==payStatus){
            if(!overflag){//
                btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','订单详情','/manager/order/to-ordercache-info?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">去提交订单</button>");

            }
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm layui-btn-danger\" onclick=\"toDelete('"+MyEncryptUtil.encry(orderNo)+"')\">删除订单</button>");

        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && (ShipStatusEnum.ShipStatus0.getCode()==shipStatus ||ShipStatusEnum.ShipStatus1.getCode()==shipStatus)){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm layui-btn-danger\" onclick=\"toCancel('"+MyEncryptUtil.encry(orderNo)+"')\">取消订单</button>");
        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus2.getCode()==shipStatus){
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toMakeSure('"+MyEncryptUtil.encry(orderNo)+"')\">确认签收</button>");
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','300px','申请退还','/manager/review/to-exit-back-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">申请退还</button>");

        }else if(PayStatusEnum.PayStatus1.getCode()==payStatus && ShipStatusEnum.ShipStatus4.getCode()==shipStatus){
            try {
                ReviewService rs=getBean(ReviewService.class);
                ReviewPO rpo=rs.getReviewByBusinessCode(orderNo);
                btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','580px','审批情况','/manager/review/to-review-info-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">审批情况</button>");
                if(rpo.getDrReviewStatus()== ReviewStatusEnum.ReviewStatus1.getCode()){
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','270px','填写寄件信息','/manager/order/to-order-exit-back-edit-mail-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">填写寄件信息</button>");

                }else if(rpo.getDrReviewStatus()== ReviewStatusEnum.ReviewStatus2.getCode()){
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toMakeSure('"+MyEncryptUtil.encry(orderNo)+"')\">确认签收</button>");
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','300px','再申请退还','/manager/review/to-exit-back-html?orderNo="+MyEncryptUtil.encry(orderNo)+"')\">再申请退还</button>");

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return btnBuilder.toString();
    }
    public static Integer getPrestoreByM(Integer mid){
       WebApplicationContext wa= getWebapplication();
       OrderService orderService=wa.getBean(OrderService.class);
      List<Map> list= orderService.getOrderMaterialByPayStatusAndShipStatusAndMid( ShipStatusEnum.ShipStatus0.getCode(),mid);
      Integer pm=0;
      if(list!=null && !list.isEmpty()){
          for(Map map:list){

              pm=pm+(Integer) map.get("materialBuyNumber");

          }
      }
      return pm;
    }
    public static Integer getPrestoreByM(Integer mid,List<Map> om){
        Integer pm=0;
        if(om!=null && !om.isEmpty()){
            Integer materialId=0;

            for(Map map:om){
                materialId=(Integer)map.get("materialId");
                if(materialId==mid || mid.equals(materialId)){
                    pm=pm+(Integer) map.get("materialBuyNumber");
                }
            }

        }
        return pm;
       /* WebApplicationContext wa= getWebapplication();

        //wa.getBean()
        Cache cache= CacheUtil.getCacheManager().getCache(Constant.CACHE_NAME_PC_SHOPPING_CART);
        List<String> keys=cache.getKeys();
        Element element=null;
        Map cacheMap=null;
        List<OrderMaterialPO> list=null;
        Integer prestoreNum=0;
        for(String key:keys){
            if(key.startsWith("store-order-")){
              element=cache.get(key);
              cacheMap=(Map) element.getObjectValue();
              list=(List) cacheMap.get("list");
              for(OrderMaterialPO omp:list){
                  if(omp.getId()==mid || omp.getId().equals(mid)){
                      prestoreNum=prestoreNum+ omp.getMaterialBuyNumber();
                  }
              }

            }
        }*/
    }
}
