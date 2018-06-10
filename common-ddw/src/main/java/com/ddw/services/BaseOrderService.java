package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.OrderPO;
import com.ddw.beans.OrderViewPO;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.enums.ShipStatusEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


public class BaseOrderService extends CommonService {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private OrderViewService orderViewService;

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
    public ResponseVO pulbicUpdateOrderPayStatus(PayStatusEnum payStatusEnum, String orderNo)throws Exception{
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号为空",null);
        }

        Map map=new HashMap();
        map.put("doPayStatus",payStatusEnum.getCode());
        OrderPO cacheOrder=this.getCacheOrder(orderNo);
        Integer doType=cacheOrder.getDoType();
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",OrderUtil.getOrderId(orderNo));
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
                po.setShipStatus(ShipStatusEnum.ShipStatus5.getCode());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType4.getCode().equals(doType)){
                String ub =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                Integer earnest=(Integer)CacheUtil.get("pay","bidding-earnest-pay-"+ub);

                CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
                CacheUtil.delete("pay","pre-pay-"+orderNo);
                CacheUtil.delete("pay","orderObject-"+orderNo);
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
                po.setShipStatus(ShipStatusEnum.ShipStatus5.getCode());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
            }else if(OrderTypeEnum.OrderType5.getCode().equals(doType)){

                List<String> ubs =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(ubs==null){
                    throw new GenException("更新竞价金额支付状态失败");
                }
                Map payMap=null;
                Map updateMap=null;
                //goddessUserId
                OrderViewPO po=null;
                for(String ub:ubs){
                    payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
                    if(payMap==null){
                        throw new GenException("更新竞价金额支付状态失败");
                    }
                    updateMap=new HashMap();
                    updateMap.put("endTime",DateUtils.addMinutes(new Date(),(Integer) payMap.get("time")));
                    this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",payMap.get("code"));
                    this.incomeService.commonIncome((Integer) payMap.get("goddessUserId"),Integer.parseInt((String) payMap.get("needPayPrice")), IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType5,orderNo);
                    po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName(OrderTypeEnum.OrderType5.getName());
                    po.setHeadImg(null);
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType5.getCode());

                    po.setPrice(Integer.parseInt((String) payMap.get("needPayPrice")));
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(ShipStatusEnum.ShipStatus0.getCode());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                }
                ubs.forEach(a->CacheUtil.delete("pay","bidding-pay-"+a));
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType1.getCode().equals(doType)){
                List<Map> list=getGoodsPruduct(orderNo);
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
                    po.setName((String)m.get("dghDesc"));
                    po.setHeadImg((String)m.get("headImg"));
                    po.setNum((Integer) m.get("productBuyNumber"));
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType1.getCode());

                    po.setPrice((Integer) m.get("productUnitPrice"));
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(ShipStatusEnum.ShipStatus0.getCode());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);

                }
                CacheUtil.delete("pay","goodsPru-order-"+orderNo);

            }else if(OrderTypeEnum.OrderType6.getCode().equals(doType)){
                String jsonStr =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(jsonStr==null){
                    throw new GenException("更新礼物支付状态失败");
                }
                JSONObject json=JSONObject.parseObject(jsonStr);
                Integer goddUserId=json.getInteger("goddessUserId");
                Integer cost=json.getInteger("cost");
                this.incomeService.commonIncome(goddUserId,cost,IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType6,orderNo);
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(json.getString("name"));
                po.setHeadImg(json.getString("headImg"));
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(cost);
                po.setOrderType(OrderTypeEnum.OrderType6.getCode());

                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(ShipStatusEnum.ShipStatus5.getCode());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","pre-pay-"+orderNo);
            }
            CacheUtil.put("pay","order-"+orderNo,"success");
            return new ResponseVO(1,"更新支付状态成功",null);

        }
        CacheUtil.put("pay","order-"+orderNo,"fail");
        return new ResponseVO(-2,"更新支付状态失败",null);


    }
    public List getGoodsPruduct(String orderNo)throws Exception{
        List list=(ArrayList) CacheUtil.get("pay","goodsPru-order-"+orderNo);
        if(list!=null)return list;
        Map search=new HashMap();
        search.put("orderNo",orderNo);
        CommonSearchBean csb=new CommonSearchBean("ddw_goods_product",null,"t1.dghActivityPrice,t1.dghSalesPrice,t1.dghDesc,t1.id,ct0.fileImgIcoPath headImg",null,null,search,new CommonChildBean("ddw_goods","id","dghGoodsId",null));
        return this.getCommonMapper().selectObjects(csb);
    }

}
