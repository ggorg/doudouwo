package com.ddw.servies;

import com.ddw.beans.MaterialPO;
import com.ddw.beans.OrderMaterialPO;
import com.ddw.beans.OrderPO;
import com.ddw.enums.*;
import com.ddw.util.Constant;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService extends CommonService {

    @Autowired
    private MaterialService materialService;

    //private Sysm

    /**
     * 确认订购
     * @param userid
     * @param storeid
     * @param mids
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO prestoreOrder(Map userMap,Integer storeid,Integer[] mids)throws Exception{
        if(mids==null){
            return new ResponseVO(-2,"请选择要订购的材料",null);
        }
        List list=Arrays.asList(mids);
        List<Map> mList=materialService.getMaterialByCache(storeid);
        if(mList==null){
            return new ResponseVO(-2,"抱歉，购物车的材料已经超过有效期，请重新入购",null);

        }

        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),24));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoCustomerUserId((Integer) userMap.get("id"));
        orderPO.setDoSellerId(-1);
        orderPO.setDoCustomerStoreId(storeid);
        orderPO.setDoPayType(PayTypeEnum.PayType3.getCode());
        orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());
        orderPO.setDoCustomerType(OrderCustomerTypeEnum.OrderCustomerType4.getCode());
        orderPO.setDoType(OrderTypeEnum.OrderType2.getCode());
        orderPO.setCreater((String)userMap.get("creater"));
        ResponseVO<Integer> insertResponseVO=this.commonInsert("ddw_order",orderPO);
        if(insertResponseVO.getReCode()!=1){
            throw new  GenException("订单生成失败");
        }
/*        StringBuilder orderNo=new StringBuilder();
        orderNo.append("01");//表示01库，以后可能会分库
        orderNo.append("01");//表示01表，以后可能会分表
        orderNo.append(orderPO.getDoOrderDate());//时间
        orderNo.append("0").append(orderPO.getDoType());//订单类型
        orderNo.append("0").append(orderPO.getDoPayType());//支付类型
        orderNo.append(insertResponseVO.getData());//订单id号*/
        String orderNo= OrderUtil.createOrderNo(orderPO.getDoOrderDate(),orderPO.getDoType(),orderPO.getDoPayType(),insertResponseVO.getData());
        Integer id=null;
        Integer num=null;
        MaterialPO mpo=null;

        OrderMaterialPO orderMaterialPO=null;
        ResponseVO  orderMaterialRes=null;
        Map cacheMap=new HashMap();
        cacheMap.put("order",orderPO);
        cacheMap.put("orderNo",orderNo);
        List morder=new ArrayList();
        cacheMap.put("list",morder);
        Integer cp=0;
        for(Map m:mList){
            id=(Integer) m.get("id");
            num=(Integer) m.get("num");
            mpo=new MaterialPO();
            PropertyUtils.copyProperties(mpo,this.materialService.getById(id));
            if(mpo.getDmCurrentCount()<num){
                return new ResponseVO(-2,"抱歉，所选的材料["+mpo.getDmName()+"]库存只剩:+"+mpo.getDmCurrentCount()+",请重新购选",null);

            }
            orderMaterialPO=new OrderMaterialPO();
            orderMaterialPO.setCreateTime(new Date());
            orderMaterialPO.setUpdateTime(new Date());
            orderMaterialPO.setMaterialId(id);
            orderMaterialPO.setMaterialBuyNumber(num);
            orderMaterialPO.setMaterialName(mpo.getDmName());
            orderMaterialPO.setMaterialUnitPrice(mpo.getDmSales());
            orderMaterialPO.setMaterialCountPrice(mpo.getDmSales()*num);
            orderMaterialPO.setOrderId(insertResponseVO.getData());
            orderMaterialPO.setOrderNo(orderNo);
            cp=cp+mpo.getDmSales()*num;
            orderMaterialRes=this.commonInsert("ddw_order_material",orderMaterialPO);
            if(orderMaterialRes.getReCode()!=1){
                throw new  GenException("订单生成失败");
            }
            morder.add(orderMaterialPO);


        }
        cacheMap.put("countPrice",cp);
        CacheUtil.delete(Constant.CACHE_NAME_PC_SHOPPING_CART,"storeId-"+storeid);
        CacheUtil.put(Constant.CACHE_NAME_PC_SHOPPING_CART,"store-order-"+storeid+"-"+orderNo.toString(),cacheMap);
        return new ResponseVO(1,"订单生成成功",orderNo.toString());
    }
    @Cacheable(value ="pcShoppingCart",key="'store-order-'+#storeid+'-'+#orderNo" )
    public Map getOrderByStoreAndOrderNo(Integer storeid,String orderNo)throws Exception{
        Integer orderid=OrderUtil.getOrderId(orderNo);
        String orderTime=OrderUtil.getOrderTime(orderNo);
        Map condition=new HashMap();
        condition.put("id",orderid);
        condition.put("doOrderDate",orderTime);
        OrderPO orderPO=this.commonObjectBySearchCondition("ddw_order",condition,OrderPO.class);
        Map cacheMap=new HashMap();
        cacheMap.put("order",orderPO);
        cacheMap.put("orderNo",orderNo);
        List morder=new ArrayList();
        cacheMap.put("list",morder);
        List<Map> list=this.commonObjectsBySingleParam("ddw_order_material","orderNo",orderNo);
        OrderMaterialPO orderMaterialPO=null;
        Integer countPrice=0;
        for(Map map:list){
            orderMaterialPO=new OrderMaterialPO();
            PropertyUtils.copyProperties(orderMaterialPO,map);
            morder.add(orderMaterialPO);
            countPrice=countPrice+orderMaterialPO.getMaterialCountPrice();
        }
        cacheMap.put("countPrice",countPrice);
        return cacheMap;
    }
    /**
     * 根据门店查询原材订单
     * @param storeId
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page findOrderByStore(Integer storeId,Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",OrderTypeEnum.OrderType2.getCode());
        condition.put("doCustomerStoreId",storeId);
        condition.put("doSellerId",-1);
        condition.put("doCustomerType", OrderCustomerTypeEnum.OrderCustomerType4.getCode());
        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }

    /**
     * 根据总部查询
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page findOrderByHq(Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",OrderTypeEnum.OrderType2.getCode());
        condition.put("doSellerId",-1);
        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }
    /**
     * 根据会员ID
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page findOrderByUserId(Integer userid,Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",OrderTypeEnum.OrderType1.getCode());
        condition.put("doCustomerUserId",userid);
        condition.put("doCustomerStoreId",-1);

        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }
    /**
     * 根据卖家门店查询
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page findOrderBySellerStore(Integer storeId,Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",OrderTypeEnum.OrderType1.getCode());
        condition.put("doSellerId",storeId);

        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }



}
