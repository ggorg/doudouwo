package com.ddw.services;

import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


public class BaseOrderService extends CommonService {
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
        Integer doType=this.commonSingleFieldBySingleSearchParam("ddw_order","id", OrderUtil.getOrderId(orderNo),"doType",Integer.class);
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
            }else if(OrderTypeEnum.OrderType4.getCode().equals(doType)){
                String ub =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType5.getCode().equals(doType)){

                List<String> ubs =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(ubs==null){
                    throw new GenException("更新竞价金额支付状态失败");
                }
                Map payMap=null;
                Map updateMap=null;
                for(String ub:ubs){
                    payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
                    if(payMap==null){
                        throw new GenException("更新竞价金额支付状态失败");
                    }
                    updateMap=new HashMap();
                    updateMap.put("endTime",DateUtils.addMinutes(new Date(),(Integer) payMap.get("time")));
                    this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",payMap.get("code"));
                }
                ubs.forEach(a->CacheUtil.delete("pay","bidding-pay-"+a));
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType1.getCode().equals(doType)){
                List<Map> list=getGoodsPruduct(orderNo);
                Map search=null;
                Map setMap=null;
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

                }
                CacheUtil.delete("pay","goodsPru-order-"+orderNo);

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
        return this.commonObjectsBySingleParam("ddw_order_product","orderNo",orderNo);
    }
}
