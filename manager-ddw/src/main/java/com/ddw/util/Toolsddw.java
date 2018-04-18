package com.ddw.util;

import com.ddw.beans.OrderMaterialPO;
import com.ddw.enums.PayStatusEnum;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.OrderService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Tools;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
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
    public static String getOrderStatusInfo(Integer payStatus,Integer shipStatus,Date orderEndTime){
        boolean overflag=isOverTime(orderEndTime);
        if(PayStatusEnum.PayStatus0.getCode()==payStatus){
            if(overflag){
                return "订单已超时";
            }else{
                return "没提交申请";
            }
        }else if(payStatus>PayStatusEnum.PayStatus0.getCode() && shipStatus!=ShipStatusEnum.ShipStatus5.getCode()){
            return "进行中";
        }else{
            return "结束";
        }

    }
    public static Integer getPrestoreByM(Integer mid){
       WebApplicationContext wa= getWebapplication();
       OrderService orderService=wa.getBean(OrderService.class);
      List<Map> list= orderService.getOrderMaterialByPayStatusAndShipStatusAndMid(PayStatusEnum.PayStatus0.getCode(), ShipStatusEnum.ShipStatus0.getCode(),mid);
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
