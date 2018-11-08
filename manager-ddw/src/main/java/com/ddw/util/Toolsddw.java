package com.ddw.util;

import com.ddw.beans.ReviewPO;
import com.ddw.beans.StorePO;
import com.ddw.enums.*;
import com.ddw.servies.OrderService;
import com.ddw.servies.ReviewService;
import com.ddw.servies.RoleService;
import com.ddw.servies.StoreService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Tools;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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

    @Override
    public String getFullUseTime(Date startDate, Date CompleteDate) {
        return super.getFullUseTime(startDate, CompleteDate);
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
    public static String getBtnMsgByStore(Integer payStatus,Integer shipStatus,Date orderEndTime,String orderNo) {
        StringBuilder btnBuilder=new StringBuilder();

        if (PayStatusEnum.PayStatus1.getCode() == payStatus && ShipStatusEnum.ShipStatus0.getCode() == shipStatus) {
            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toAccept('" + MyEncryptUtil.encry(orderNo) + "')\">接受订单</button>");

        } else if (PayStatusEnum.PayStatus1.getCode() == payStatus && ShipStatusEnum.ShipStatus1.getCode() == shipStatus) {

            btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"toMakeSure('" + MyEncryptUtil.encry(orderNo) + "')\">确认发货</button>");



        }
        return btnBuilder.toString();
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
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','退还受理','/manager/review/to-review-by-hq-html?id="+rpo.getId()+"')\">退换受理</button>");

                }else{
                    btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','580px','审批情况','/manager/review/to-review-info-html?id="+rpo.getId()+"')\">审批情况</button>");

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
                btnBuilder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('500px','580px','审批情况','/manager/review/to-review-info-html?id="+rpo.getId()+"')\">审批情况</button>");
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
    public static String commonReviewBtn(Integer id,String businessCode,Integer reviewStatus){
        try {
            StringBuilder builder=new StringBuilder();
            StorePO spo=getWebapplication().getBean(StoreService.class).getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(ReviewStatusEnum.ReviewStatus0.getCode().equals(reviewStatus)){
                if(spo!=null){
                    //                            <button class="layui-btn layui-btn-sm" th:onclick="'openDialog(\'650px\',\'580px\',\'审批处理\',\'/manager/review/to-review-by-hq-html??id='+${obj['id']}+'\')'"  >去审批</button>
                    builder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','审批处理','/manager/review/to-review-by-store-html?id="+id+"')\">去审批</button>");

                }else{
                    List roleList=getWebapplication().getBean(RoleService.class).getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
                    if(roleList!=null && roleList.size()>0){
                        builder.append("<button class=\"layui-btn layui-btn-sm\" onclick=\"openDialog('650px','580px','审批处理','/manager/review/to-review-by-hq-html?id="+id+"')\">去审批</button>");
                    }
                }
                return builder.toString();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
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
        Cache cache= CacheUtil.getCacheManager().getCache(LiveRadioConstant.CACHE_NAME_PC_SHOPPING_CART);
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

    public static String  createCouponSelect(List<Map> coupons,Integer couponId){
        Integer dcType=0;
        Integer dcMoney=0;
        Integer dcMinPrice=0;
        StringBuilder builder=new StringBuilder();
        boolean flag = false;//无优惠券标志,false无数据,true有数据
        builder.append("<td>");
        builder.append("<select name='couponId' required='required' lay-filter='select'>");
        builder.append("<option value=''></option>");
        if(coupons!=null){
            Integer id=null;
            String name=null;

            for(Map m:coupons){
                id=(Integer) m.get("id");
                name=(String) m.get("dcName");
                dcType=(Integer) m.get("dcType");
                dcMoney=(Integer) m.get("dcMoney");
                dcMinPrice=(Integer) m.get("dcMinPrice");
                builder.append("<option");
                if(id.equals(couponId)||id==couponId){
                    builder.append(" selected='selected'");
                }
                builder.append(" data-dcType='").append(CouponTypeEnum.getName(dcType)).append("'");
                builder.append(" data-dcMoney='").append(dcMoney).append("'");
                builder.append(" data-dcMinPrice='").append(dcMinPrice).append("'");
                builder.append(" value='");
                builder.append(id);
                builder.append("' >");
                builder.append(name);
                builder.append("</option>");
            }
        }
        builder.append("</select>");
        if(coupons!=null){
            Integer id=null;
            for(Map m:coupons){
                id=(Integer) m.get("id");
                dcType=(Integer) m.get("dcType");
                dcMoney=(Integer) m.get("dcMoney");
                dcMinPrice=(Integer) m.get("dcMinPrice");
                if(id.equals(couponId)||id==couponId){
                    flag = true;
                    builder.append("<td>");
                    builder.append(CouponTypeEnum.getName(dcType));
                    builder.append("</td>");
                    builder.append("<td>");
                    builder.append(dcMoney);
                    builder.append("</td>");
                    builder.append("<td>");
                    builder.append(dcMinPrice);
                    builder.append("</td>");
                }
            }
        }
        if(flag==false){
            builder.append("<td>");
            builder.append("</td>");
            builder.append("<td>");
            builder.append("</td>");
            builder.append("<td>");
            builder.append("</td>");
        }
        return builder.toString();

    }

    public static String  createStoreMaterialSelect(List<Map> materials,Integer materialId){
        Integer dsCountNetWeight=0;
        String dsUnit="";
        String tdText="";
        StringBuilder builder=new StringBuilder();

        builder.append("<td>");
        builder.append("<select name='materialId' required='required' lay-filter='select'>");
        builder.append("<option value=''></option>");
        if(materials!=null){
            Integer id=null;
            String name=null;

            for(Map m:materials){
                id=(Integer) m.get("id");
                name=(String) m.get("dmName");
                dsCountNetWeight=(Integer) m.get("dsCountNetWeight");
                dsUnit=(String) m.get("dsUnit");
                builder.append("<option");
                if(id.equals(materialId)||id==materialId){
                    builder.append(" selected='selected'");
                    tdText=dsCountNetWeight.toString()+dsUnit;
                }
                builder.append(" data-weight='").append(dsCountNetWeight).append(dsUnit).append("'");
                builder.append(" value='");
                builder.append(id);
                builder.append("' >");
                builder.append(name);
                builder.append("</option>");
            }
        }
        builder.append("</select>");
        builder.append("</td>");
        builder.append("<td>");
        if(StringUtils.isNotBlank(tdText))builder.append(tdText);
        builder.append("</td>");
        return builder.toString();

    }
    public static String createLiveRadioUrl(){
        String refererKey="bb136f00754697b5ea85bb6ee69a1946";
        String apiKey="aaceb72fe5a8a474bcca9c2576b92a58";
        // appid: 1255887407	 bizid : 23115
        //txSecret = MD5(refererKey+ stream_id + txTime)
        String bizid="23115";
        String streamid="23115_"+ RandomStringUtils.randomAlphabetic(10);
        String pushUrl="rtmp://23115.livepush.myqcloud.com/live/23115_test001?txSecret=xxx&txTime=5C2A3CFF";
        String txTime=Long.toHexString(DateUtils.addHours(new Date(),24).getTime()/1000).toUpperCase();
        try {
            txTime= Long.toHexString(DateUtils.parseDate("2018-04-25 23:59:59","yyyy-MM-dd HH:mm:ss").getTime()/1000).toUpperCase();

        }catch (Exception e){
            e.printStackTrace();
        }
       // DigestUtils

      //  Hex.decodeHex(new Date().getTime()/1000)
        String md5str=DigestUtils.md5Hex(refererKey+streamid+txTime);
        StringBuilder url=new StringBuilder();
        url.append("rtmp://");
        url.append(bizid);
        url.append(".livepush.myqcloud.com/live/");
        url.append(streamid);
        System.out.println("播放："+url.toString().replace("livepush","liveplay"));

        url.append("?bizid=").append(bizid).append("&");

        url.append("txSecret=").append(md5str);
        url.append("&txTime=").append(txTime);
        System.out.println("推流："+url.toString());

        try {
            System.out.println(Long.toHexString(DateUtils.parseDate("2016-07-30 11:13:45","yyyy-MM-dd HH:mm:ss").getTime()/1000).toUpperCase());
            System.out.println();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getPlateStr(List<Map> plates,Integer plateId){
        if(plateId==null || plates==null){
            return "无";
        }
        for(Map m:plates){
           if( m.get("id").equals(plateId)){
               return m.get("dgtName").toString();
           }
        }
        return "无";
    }
    public static void main(String[] args) {
        createLiveRadioUrl();
    }
}
