package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ExitOrderPO;
import com.ddw.beans.OrderPO;
import com.ddw.beans.OrderViewPO;
import com.ddw.enums.*;
import com.ddw.util.PayApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


public class BaseOrderService extends CommonService {

    @Autowired
    protected IncomeService incomeService;

    @Autowired
    protected OrderViewService orderViewService;

    @Autowired
    protected BaseConsumeRankingListService baseConsumeRankingListService;

    @Autowired
    private StraregyService straregyService;

    public OrderPO getCacheOrder(String orderNo)throws Exception{
        String objStr=(String) CacheUtil.get("pay","orderObject-"+orderNo);
        if(objStr!=null){
            return JSONObject.parseObject(objStr,OrderPO.class);
        }
        return this.commonObjectBySingleParam("ddw_order","id",OrderUtil.getOrderId(orderNo),OrderPO.class);

    }

    /**
     * 调用微信或者支付宝支付接口回调时候执行的
     * @param payStatusEnum
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO pulbicUpdateOrderPayStatus(PayStatusEnum payStatusEnum, String orderNo,OrderPO...orderPO)throws Exception{
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号为空",null);
        }


        OrderPO cacheOrder=null;
        ResponseVO res=null;

        if(orderPO!=null && orderPO.length>0){
            cacheOrder=orderPO[0];
            res=new ResponseVO(1,null,null);
        }else{
            cacheOrder=this.getCacheOrder(orderNo);
            Map map=new HashMap();
            map.put("doPayStatus",payStatusEnum.getCode());
            res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",OrderUtil.getOrderId(orderNo));
        }
        Integer doType=cacheOrder.getDoType();
        if(res.getReCode()==1){
            if(OrderTypeEnum.OrderType3.getCode().equals(doType)){
                Map mapRecharge=this.commonObjectBySingleParam("ddw_order_recharge","orderNo",orderNo);
                Integer userid=(Integer) mapRecharge.get("creater");
                Integer dorCost=(Integer) mapRecharge.get("dorCost");
                Map setParams=new HashMap();
                setParams.put("money",dorCost);
                Map condition=new HashMap();
                condition.put("userId",userid);
                ResponseVO wres=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,condition,"version",new String[]{"money"});
                if(wres.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                //根据充值金额匹配会员策略
                straregyService.recharge(dorCost,userid);
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType3.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderType(OrderTypeEnum.OrderType3.getCode());
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(dorCost);
                po.setUserId(userid);
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType8.getCode().equals(doType)){
                Map mapRecharge=this.commonObjectBySingleParam("ddw_order_doubi","orderNo",orderNo);
                Integer userid=(Integer) mapRecharge.get("creater");
                Integer dodDoubiNum=(Integer) mapRecharge.get("dodDoubiNum");
                Map setParams=new HashMap();
                setParams.put("coin",dodDoubiNum);
                Map condition=new HashMap();
                condition.put("userId",userid);
                ResponseVO wres=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,condition,"version",new String[]{"coin"});
                if(wres.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType8.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderType(OrderTypeEnum.OrderType8.getCode());
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(cacheOrder.getDoCost());
                po.setUserId(userid);
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                Map doubiRecord=new HashMap();
                doubiRecord.put("coin",dodDoubiNum);
                doubiRecord.put("userId",userid);
                doubiRecord.put("type", DoubiRecordTypeEnum.Type0.getCode());
                doubiRecord.put("createTime",new Date());
                doubiRecord.put("updatetime",new Date());
                ResponseVO resIn=this.commonInsertMap("ddw_my_wallet_doubi_record",doubiRecord);
                if(resIn.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType4.getCode().equals(doType)){
                String ub =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                Integer earnest=(Integer)CacheUtil.get("pay","bidding-earnest-pay-"+ub);


                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType4.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(earnest);
                po.setOrderType(OrderTypeEnum.OrderType4.getCode());
                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
                CacheUtil.delete("pay","pre-pay-"+orderNo);
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType5.getCode().equals(doType)){

                List<String> ubs =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(ubs==null){
                    throw new GenException("更新竞价金额支付状态失败");
                }
                Map payMap=null;

                //goddessUserId
                OrderViewPO po=null;
                List<String> bid_goddessid=new ArrayList();
                Integer gid=null;
                Integer needPayPrice=0;

                for(String ub:ubs){
                    payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
                    if(payMap==null){
                        throw new GenException("更新竞价金额支付状态失败");
                    }
                   /*
                    Map updateMap=null;updateMap=new HashMap();
                    updateMap.put("endTime",DateUtils.addMinutes(new Date(),(Integer) payMap.get("time")));
                    this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",payMap.get("code"));
*/
                    gid=(Integer) payMap.get("goddessUserId");

                    bid_goddessid.add(payMap.get("code").toString()+"-"+gid);
                    needPayPrice=Integer.parseInt((String) payMap.get("needPayPrice"));
                    this.incomeService.commonIncome(gid,needPayPrice, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType5,orderNo);
                    this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),gid,needPayPrice,IncomeTypeEnum.IncomeType1);


                    po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName(OrderTypeEnum.OrderType5.getName());
                    po.setHeadImg(null);
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType5.getCode());

                    po.setPrice(needPayPrice);
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                }
                bid_goddessid.forEach(a->{
                    CacheUtil.put("pay","bidding-finish-pay-"+a,orderNo);
                   // CacheUtil.delete("pay","bidding-pay-"+a);
                });
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType9.getCode().equals(doType)){

                Map renewMap=(Map) CacheUtil.get("pay","pre-pay-"+orderNo);

                if(renewMap==null){
                    throw new GenException("续费失败");
                }


                Integer needPayPrice=(Integer)renewMap.get("price");
                Integer time=(Integer)renewMap.get("time");

                Integer gid=(Integer) renewMap.get("goddessUserId");
                this.incomeService.commonIncome(gid,needPayPrice, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType9,orderNo);
                this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),gid,needPayPrice,IncomeTypeEnum.IncomeType1);


                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType9.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setOrderType(OrderTypeEnum.OrderType9.getCode());
                po.setPrice(needPayPrice);
                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                Map setMap=new HashMap();
                setMap.put("price",needPayPrice);
                setMap.put("times",time);
                setMap.put("endTime", DateUtils.addMinutes((Date)renewMap.get("endTime"),time));
                Map searchMap=new HashMap();
                searchMap.put("id",renewMap.get("bidCode"));
                this.commonCalculateOptimisticLockUpdateByParam("ddw_goddess_bidding",setMap,searchMap,"version",new String[]{"price","times"});
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType1.getCode().equals(doType)){
                List<Map> list=(List) CacheUtil.get("pay","goodsPru-order-"+orderNo);
                Map search=null;
                Map setMap=null;
                OrderViewPO po=null;
                for(Map m:list){
                    setMap=new HashMap();
                    setMap.put("dghSaleNumber",m.get("productBuyNumber"));
                    setMap.put("updateTime",new Date());
                    search=new HashMap();
                    search.put("id",m.get("productId"));
                    ResponseVO r=this.commonCalculateOptimisticLockUpdateByParam("ddw_goods_product",setMap,search,"dghVersion",new String[]{"dghSaleNumber"});
                    if(r.getReCode()!=1){
                        CacheUtil.put("pay","order-"+orderNo,"fail");

                        throw new GenException("更新货品销量失败");
                    }
                    po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName((String)m.get("productName"));
                    po.setHeadImg((String)m.get("headImg"));
                    po.setNum((Integer) m.get("productBuyNumber"));
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType1.getCode());

                    po.setPrice((Integer) m.get("productUnitPrice"));
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);

                }
                CacheUtil.delete("pay","goodsPru-order-"+orderNo);

            }else if(OrderTypeEnum.OrderType7.getCode().equals(doType)){
                List<Map> ticketList =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                OrderViewPO po=null;
                for(Map ticketMap:ticketList){
                    po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName((String)ticketMap.get("ticketName"));
                    po.setHeadImg(null);
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setPrice((Integer) ticketMap.get("ticketPrice"));
                    po.setOrderType(OrderTypeEnum.OrderType7.getCode());

                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                }

            }/*else if(OrderTypeEnum.OrderType6.getCode().equals(doType)){
                String jsonStr =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(jsonStr==null){
                    throw new GenException("更新礼物支付状态失败");
                }
                JSONObject json=JSONObject.parseObject(jsonStr);
                Integer goddUserId=json.getInteger("goddessUserId");
                JSONArray giftArray=json.getJSONArray("giftList");
                JSONObject jsonGift=null;
                for(int i=0;i<giftArray.size();i++){
                    jsonGift=giftArray.getJSONObject(i);
                    Integer cost=jsonGift.getInteger("cost");
                    OrderViewPO po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName(jsonGift.getString("name"));
                    po.setHeadImg(jsonGift.getString("headImg"));
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setPrice(cost);
                    po.setOrderType(OrderTypeEnum.OrderType6.getCode());

                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                    if(goddUserId>-1){
                        this.incomeService.commonIncome(goddUserId,cost,IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType6,orderNo);
                        this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),goddUserId,cost,IncomeTypeEnum.IncomeType1);
                    }
                }

                CacheUtil.delete("pay","pre-pay-"+orderNo);
            }*/
            if(StringUtils.isNotBlank(cacheOrder.getDoCouponNo())){
                Map setParam=new HashMap();
                setParam.put("used",1);
                Map search=new HashMap();
                search.put("couponId",Integer.parseInt(cacheOrder.getDoCouponNo()));
                search.put("userId",cacheOrder.getDoCustomerUserId());
                ResponseVO updatevo=this.commonUpdateByParams("ddw_userinfo_coupon",setParam,search);
                if(updatevo.getReCode()==-1){
                    throw new GenException("更新优惠卷失败");
                }
            }
            CacheUtil.put("pay","order-"+orderNo,"success");

            return new ResponseVO(1,"更新支付状态成功",null);

        }
        CacheUtil.put("pay","order-"+orderNo,"fail");
        return new ResponseVO(-2,"更新支付状态失败",null);


    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO baseExitOrder(List<Integer> orderIds)throws Exception{
        if(orderIds==null){
            return new ResponseVO(-2,"参数异常",null);

        }else if(orderIds.isEmpty()){
            return new ResponseVO(1,"成功",null);

        }
        ExitOrderPO exitOrderPO=null;
        Map searchMap=new HashMap();
        searchMap.put("id,in",orderIds.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
        searchMap.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
        List<Map> orders=this.commonObjectsBySearchCondition("ddw_order",searchMap);
        for(Map o:orders){
            // orderPO=this.commonObjectBySingleParam("ddw_order","id",OrderUtil.getOrderId(o),OrderPO.class);
            exitOrderPO=new ExitOrderPO();
            exitOrderPO.setCreater((Integer) o.get("doCustomerUserId"));
            exitOrderPO.setCreaterName((String) o.get("creater"));
            exitOrderPO.setCreateTime(new Date());
            exitOrderPO.setExitCost((Integer) o.get("doCost"));
            exitOrderPO.setTotalCost(exitOrderPO.getExitCost());
            exitOrderPO.setOrderId((Integer) o.get("id"));
            exitOrderPO.setOrderType((Integer)o.get("doType"));
            exitOrderPO.setOrderNo(OrderUtil.createOrderNo((String)o.get("doOrderDate"),(Integer) o.get("doType"),(Integer) o.get("doPayType"),exitOrderPO.getOrderId()));
            exitOrderPO.setExitOrderNo(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss")+ RandomStringUtils.randomNumeric(6));
            ResponseVO inserRes=this.commonInsert("ddw_exit_order",exitOrderPO);
            if(inserRes.getReCode()!=1){
                throw new GenException("新建退订单失败");
            }
            if(PayTypeEnum.PayType1.getCode().equals((Integer) o.get("doPayType"))){
                Map<String,String> callMap= PayApiUtil.reqeustWeiXinExitOrder(exitOrderPO.getOrderNo(),exitOrderPO.getExitOrderNo(),exitOrderPO.getExitCost(),exitOrderPO.getExitCost());
                if(callMap!=null && "FAIL".equals(callMap.get("return_code"))){
                    throw new GenException("申请退款失败");
                }else if(callMap!=null && "SUCCESS".equals(callMap.get("return_code")) && "SUCCESS".equals(callMap.get("result_code"))){
                    CacheUtil.put("pay","weixin-refund-"+o,exitOrderPO.getExitOrderNo());
                }

            }else if(PayTypeEnum.PayType2.getCode().equals((Integer) o.get("doPayType"))){
                ResponseVO resvo=PayApiUtil.requestAliExitOrder(exitOrderPO.getOrderNo(),exitOrderPO.getExitCost());
                if(resvo.getReCode()==1){
                    Map map=new HashMap();
                    map.put("doPayStatus",PayStatusEnum.PayStatus2.getCode());
                    ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",exitOrderPO.getOrderId());
                    map=new HashMap();
                    map.put("payStatus",PayStatusEnum.PayStatus2.getCode());
                    map.put("shipStatus",ClientShipStatusEnum.ShipStatus4.getCode());
                    this.commonUpdateBySingleSearchParam("ddw_order_view",map,"orderId",exitOrderPO.getOrderId());
                }
            }
            //ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",params,"id",orderid);

        }

        return new ResponseVO(1,"成功",null);
    }
}
