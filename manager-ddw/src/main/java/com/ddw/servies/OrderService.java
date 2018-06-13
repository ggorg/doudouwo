package com.ddw.servies;

import com.ddw.beans.MaterialPO;
import com.ddw.beans.OrderMaterialPO;
import com.ddw.beans.OrderPO;
import com.ddw.controller.ClientOrderController;
import com.ddw.enums.*;
import com.ddw.services.BaseOrderService;
import com.ddw.services.OrderViewService;
import com.ddw.util.Constant;
import com.ddw.util.Toolsddw;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonDeleteBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class OrderService extends BaseOrderService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private StoreMaterialService storeMaterialService;

    @Autowired
    private OrderViewService orderViewService;


    /**
     * 确认订购
     * @param userMap
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
        orderPO.setCreater((String)userMap.get("uNickName"));
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
        //获取订单预存的材料数量
        List orderMlist=this.getOrderMaterialByPayStatusAndShipStatus(ShipStatusEnum.ShipStatus0.getCode());
        Integer pm=null;
        for(Map m:mList){
            id=(Integer) m.get("id");
            num=(Integer) m.get("num");
            pm=Toolsddw.getPrestoreByM(id,orderMlist);
            mpo=new MaterialPO();
            PropertyUtils.copyProperties(mpo,this.materialService.getById(id));
            if((mpo.getDmCurrentCount()-pm)<num){
                return new ResponseVO(-2,"抱歉，所选的材料【"+mpo.getDmName()+"】库存只剩："+(mpo.getDmCurrentCount()-pm)+"，请重新购选",null);

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
            orderMaterialPO.setMaterialCountNetWeight(mpo.getDmNetWeight()*num);
            orderMaterialPO.setMaterialUnit(mpo.getDmUnit());
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
        CacheUtil.put(Constant.CACHE_NAME_ORDER_INFO,"store-order-"+storeid+"-"+orderNo.toString(),cacheMap);
        return new ResponseVO(1,"订单生成成功",MyEncryptUtil.encry(orderNo.toString()));
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO submitOrder(Integer storeId,String orderNoEncypt)throws Exception{
        ResponseVO res=this.updateOrderStatus(PayStatusEnum.PayStatus1.getCode(),null,storeId,orderNoEncypt);
        if(res.getReCode()==1){
            return new ResponseVO(1,"订单提交成功",null);
        }
        return res;

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO makeSureAcceptByHq(String orderNoEncypt,Integer userid)throws Exception{
        ResponseVO res=this.updateOrderStatus(null, ShipStatusEnum.ShipStatus8.getCode(),null,orderNoEncypt);
        if(res.getReCode()==1){
            handleMaterialNumByOrderNo(MyEncryptUtil.getRealValue(orderNoEncypt),false,userid);

            return new ResponseVO(1,"签收成功",null);
        }
        return new ResponseVO(-2,"签收失败",null);

    }


/*    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO submitOrder(Integer storeId,String orderNoEncypt)throws Exception{
        ResponseVO res=this.updateOrderStatus(PayStatusEnum.PayStatus1.getCode(),null,storeId,orderNoEncypt);
       if(res.getReCode()==1){
           String orderNo=MyEncryptUtil.getRealValue(orderNoEncypt);
           Map map=(Map)CacheUtil.get(LiveRadioConstant.CACHE_NAME_ORDER_INFO,"store-order-"+storeId+"-"+orderNo);
           List mlist=null;
           if(map!=null && map.size()>0){
               mlist= (List)map.get("list");
           }else{
               mlist=this.commonObjectsBySingleParam("ddw_order_material","orderNo",MyEncryptUtil.getRealValue(orderNoEncypt));
           }

           if(mlist!=null && !mlist.isEmpty()){
               Integer materid=null;
               Integer materialBuyNumber=null;
               for(Object o:mlist){
                   if(o instanceof Map){
                       Map mm=(Map)o;
                       materid=(Integer) mm.get("materialId");
                       materialBuyNumber=(Integer) mm.get("materialBuyNumber");
                   }else if(o instanceof OrderMaterialPO){
                       OrderMaterialPO om=(OrderMaterialPO)o;
                       materid=om.getMaterialId();
                       materialBuyNumber=om.getMaterialBuyNumber();
                   }
                   for()

               }
           }
          // this.materialService.
          // CacheUtil.delete(LiveRadioConstant.CACHE_NAME_PC_SHOPPING_CART,"store-order-"+storeid+"-"+orderNo);
       }
       return null;

    }*/
    /**
     * 获取（未支付和已付款状态的）订单材料
     * @param shipStatus 货品状态

     * @return
     */
    public List getOrderMaterialByPayStatusAndShipStatus(Integer shipStatus){
        return getOrderMaterialByPayStatusAndShipStatusAndMid(shipStatus,null);

    }

    /**
     * 获取（未支付和已付款状态的）指定订单材料
     * @param shipStatus 货品状态
     * @param mid 材料id
     * @return
     */
    public List getOrderMaterialByPayStatusAndShipStatusAndMid(Integer shipStatus,Integer mid){

        Map condition=new HashMap();
        if(mid!=null){
            condition.put("materialId",mid);
        }
        Map childCondition=new HashMap();
        childCondition.put("doPayStatus,<=",PayStatusEnum.PayStatus1.getCode());
        childCondition.put("doEndTime,>=",new Date());
        childCondition.put("doShipStatus",shipStatus);
        CommonChildBean ccb=new CommonChildBean("ddw_order","id","orderId",childCondition);
        CommonSearchBean csb=new CommonSearchBean("ddw_order_material",null,"t1.*",null,null,condition,ccb);
        return this.getCommonMapper().selectObjects(csb);

    }
    @Cacheable(value ="orderInfo",key="'store-order-'+#storeid+'-'+#orderNo" )
    public Map getOrderCacheByStoreAndOrderNo(Integer storeid,String orderNo)throws Exception{

        return this.getOrderByStoreAndOrderNo(orderNo,OrderTypeEnum.OrderType2);
    }
    //@Cacheable(value ="pcShoppingCart",key="'store-order-'+#storeid+'-'+#orderNo" )
    public Map getOrderByStoreAndOrderNo(String orderNo,OrderTypeEnum orderTypeEnum)throws Exception{
        Integer orderid=OrderUtil.getOrderId(orderNo);
        String orderTime=OrderUtil.getOrderTime(orderNo);
        Map condition=new HashMap();
        condition.put("id",orderid);
        condition.put("doOrderDate",orderTime);
       // condition.put("doCustomerStoreId",storeid);
        OrderPO orderPO=this.commonObjectBySearchCondition("ddw_order",condition,OrderPO.class);
        Map cacheMap=new HashMap();
        cacheMap.put("order",orderPO);
        cacheMap.put("orderNo",orderNo);
        List morder=new ArrayList();
        cacheMap.put("list",morder);
        if(OrderTypeEnum.OrderType2.getCode().equals(orderTypeEnum.getCode())){
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
        }else if(OrderTypeEnum.OrderType1.getCode().equals(orderTypeEnum.getCode())){

            List<Map> list=this.commonObjectsBySingleParam("ddw_order_product","orderNo",orderNo);
            morder.addAll(list);
            cacheMap.put("countPrice",orderPO.getDoCost());

        }else if(OrderTypeEnum.OrderType7.getCode().equals(orderTypeEnum.getCode())){

            List<Map> list=this.commonObjectsBySingleParam("ddw_order_ticket","orderNo",orderNo);
            morder.addAll(list);
            cacheMap.put("countPrice",orderPO.getDoCost());

        }



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
        condition.put("doPayStatus,>=",PayStatusEnum.PayStatus1.getCode());
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
    public Page findGoodsOrderByStore(Integer storeId,Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",OrderTypeEnum.OrderType1.getCode());
        condition.put("doSellerId",storeId);

        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }
    /**
     * 根据卖家门店查询
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page findOrder(Integer storeId,Integer pageNo,OrderTypeEnum orderTypeEnum)throws Exception{
        Map condition=new HashMap();
        condition.put("doType",orderTypeEnum.getCode());
        if(storeId!=null)condition.put("doSellerId",storeId);


        return this.commonPage("ddw_order","updateTime desc ",pageNo,10,condition);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deleteOrder(Integer storeid,String orderNoEncrypt)throws Exception{
        if(StringUtils.isBlank(orderNoEncrypt)){
            return new ResponseVO(-2,"订单号是空",null);

        }
        String orderNo=MyEncryptUtil.getRealValue(orderNoEncrypt);
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号异常",null);

        }
        Integer orderid=OrderUtil.getOrderId(orderNo);
        Map condition=new HashMap();
        condition.put("doPayStatus",PayStatusEnum.PayStatus0.getCode());
        condition.put("doCustomerStoreId",storeid);
        condition.put("id",orderid);

        int n=this.getCommonMapper().deleteObject(new CommonDeleteBean("ddw_order",condition));
        if(n>0){
            this.commonDelete("ddw_order_material","orderNo",orderNo);
            //CacheUtil.delete(LiveRadioConstant.CACHE_NAME_PC_SHOPPING_CART,"store-order-"+storeid+"-"+orderNo.toString());
        }else{
            return new ResponseVO(-2,"删除订单失败",null);
        }
        return new ResponseVO(1,"删除订单成功",null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO makerSureSendGoods(String orderNoEncypt,String doTrackingNumber,String doExpressName,Integer userid)throws Exception{
        if(StringUtils.isBlank(orderNoEncypt)){
            return new ResponseVO(-2,"订单号异常",null);
        }
        String orderNo=MyEncryptUtil.getRealValue(orderNoEncypt);
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号异常",null);

        }
        if(StringUtils.isBlank(doExpressName)){
            return new ResponseVO(-2,"快递名称为空",null);

        }
        if(StringUtils.isBlank(doTrackingNumber)){
            return new ResponseVO(-2,"运单号为空",null);

        }
        boolean hasReview=this.reviewService.hasReviewFromStore(orderNo,ReviewBusinessTypeEnum.ReviewBusinessType1,ReviewBusinessStatusEnum.orderStatus6);
        if(hasReview){
            return new ResponseVO(-2,"抱歉，当前订单被申请取消订单，请处理完再操作",null);

        }
        Integer orderid=OrderUtil.getOrderId(orderNo);
        Map params=new HashMap();
        params.put("doTrackingNumber",doTrackingNumber);
        params.put("doExpressName",doExpressName);

        params.put("doShipStatus",ShipStatusEnum.ShipStatus2.getCode());
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",params,"id",orderid);
        if(res.getReCode()==1){
            handleMaterialNumByOrderNo(orderNo,true,userid);
            return new ResponseVO(1,"确认发货成功",null);
        }
        return new ResponseVO(-2,"确认发货失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO storeEditMailInfo(String orderNoEncypt,String doExitTrackingNumber,String doExitExpressName)throws Exception{
        if(StringUtils.isBlank(orderNoEncypt)){
            return new ResponseVO(-2,"订单号异常",null);
        }
        String orderNo=MyEncryptUtil.getRealValue(orderNoEncypt);
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号异常",null);

        }
        if(StringUtils.isBlank(doExitExpressName)){
            return new ResponseVO(-2,"快递名称为空",null);

        }
        if(StringUtils.isBlank(doExitTrackingNumber)){
            return new ResponseVO(-2,"运单号为空",null);

        }

        Integer orderid=OrderUtil.getOrderId(orderNo);
        Map params=new HashMap();
        params.put("doExitTrackingNumber",doExitTrackingNumber);
        params.put("doExitExpressName",doExitExpressName);

        params.put("doShipStatus",ShipStatusEnum.ShipStatus7.getCode());
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",params,"id",orderid);
        if(res.getReCode()==1){
            return new ResponseVO(1,"确认发货成功",null);
        }
        return new ResponseVO(-2,"确认发货失败",null);


    }

    /**
     * 更新库存数量
     * @param orderno 订单号
     * @param isOut 是否出库
     * @param userid 用户id
     * @return
     * @throws Exception
     */
    public void handleMaterialNumByOrderNo(String orderno,boolean isOut,Integer userid)throws Exception{
        //ddw_material_inventory_record
        List<Map> list=this.commonObjectsBySingleParam("ddw_order_material","orderNo",orderno);
        Integer materialId=null;
        Integer materialBuyNumber=null;
        Map materialMap=null;
        Integer dmVersion=null;
        Integer dmCurrentCount=null;
        Map setParams=null;
        Map condition=null;
        for(Map map:list){
            materialId=(Integer) map.get("materialId");
            materialBuyNumber=(Integer) map.get("materialBuyNumber");
            for(int i=0;i<5;i++){
                materialMap= this.commonObjectBySingleParam("ddw_material","id",materialId);
                if(materialMap!=null && !materialMap.isEmpty()){
                    dmVersion=(Integer) materialMap.get("dmVersion");
                    dmCurrentCount=(Integer) materialMap.get("dmCurrentCount");
                    if(isOut){
                        dmCurrentCount=dmCurrentCount-materialBuyNumber;
                    }else{
                        dmCurrentCount=dmCurrentCount+materialBuyNumber;

                    }
                    setParams=new HashMap();
                    setParams.put("dmCurrentCount",dmCurrentCount);
                    setParams.put("dmVersion",dmVersion+1);
                    condition=new HashMap();
                    condition.put("id",materialId);
                    condition.put("dmVersion",dmVersion);
                   ResponseVO res= this.commonUpdateByParams("ddw_material",setParams,condition);
                   if(res.getReCode()==-2){
                       if(i==4){
                           throw new GenException("材料库存数量没法变更");
                       }
                       Thread.sleep(i * 200);
                       continue;
                   }else{
                       Map poInbound=new HashMap();
                       poInbound.put("dmiInboundNumber",0);
                       poInbound.put("dmiOutboundNumber",materialBuyNumber);
                       poInbound.put("createTime",new Date());
                       poInbound.put("creater",userid);
                       poInbound.put("dmiType",DmiTypeEnum.DmiType1.getCode());
                       poInbound.put("materialId",materialId);
                       ResponseVO rVo=this.commonInsertMap("ddw_material_inventory_record",poInbound);
                       if(rVo.getReCode()==-2){
                           throw new GenException("出库失败");
                       }
                       break;
                   }
                }else{
                    break;
                }
            }

        }

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO makeSureOrderByStore(Integer storeId,String orderNoEncypt)throws Exception{
        ResponseVO res=this.updateOrderStatus(null,ShipStatusEnum.ShipStatus3.getCode(),storeId,orderNoEncypt);
        if(res.getReCode()==1){
            List<Map> list=this.commonObjectsBySingleParam("ddw_order_material","orderNo",MyEncryptUtil.getRealValue(orderNoEncypt));
            Integer materialId=null;
            Integer dmNetWeight=null;
            Integer materialBuyNumber=null;
            String unit=null;
            for(Map map:list){
                materialId=(Integer) map.get("materialId");
                materialBuyNumber=(Integer) map.get("materialBuyNumber");
                dmNetWeight=(Integer) map.get("materialCountNetWeight");
                unit=(String) map.get("materialUnit");
                storeMaterialService.buyInMaterial(storeId,materialId,materialBuyNumber,unit,dmNetWeight);
            }

        }
        return res;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateSimpleOrderPayStatus(PayStatusEnum payStatusEnum,String orderNo)throws Exception{
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号为空",null);
        }
        Map map=new HashMap();
        map.put("doPayStatus",payStatusEnum.getCode());
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",OrderUtil.getOrderId(orderNo));

        map=new HashMap();
        map.put("payStatus",PayStatusEnum.PayStatus2.getCode());
        map.put("shipStatus",ClientShipStatusEnum.ShipStatus4.getCode());
        this.commonUpdateBySingleSearchParam("ddw_order_view",map,"orderNo",orderNo);

        return res;
    }
    /**
     * 调用微信或者支付宝支付接口回调时候执行的
     * @param payStatusEnum
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateOrderPayStatus(PayStatusEnum payStatusEnum,String orderNo)throws Exception{
       return this.pulbicUpdateOrderPayStatus(payStatusEnum,orderNo);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateClientOrderStatus(Integer payStatus,Integer shipStatus,Integer storeId,String orderNoEncypt)throws Exception{
        ResponseVO vo=this.updateOrderStatus(payStatus,shipStatus,storeId,orderNoEncypt);
        if(vo.getReCode()==1){
             vo=this.orderViewService.updateOrderView(MyEncryptUtil.getRealValue(orderNoEncypt),shipStatus);
             if(vo.getReCode()!=1){
                 throw new GenException("更新订单状态失败");
             }
            return new ResponseVO(1,"操作成功",null);
        }
        return new ResponseVO(-2,"操作失败",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateOrderStatus(Integer payStatus,Integer shipStatus,Integer storeId,String orderNoEncypt)throws Exception{
        if(StringUtils.isBlank(orderNoEncypt)){
            return new ResponseVO(-2,"订单号异常",null);
        }
        if(payStatus==null && shipStatus==null){
            return new ResponseVO(-2,"状态参数为空",null);

        }
        if(payStatus!=null && PayStatusEnum.getName(payStatus)==null){
            return new ResponseVO(-2,"付款状态值异常",null);

        }
        if(shipStatus!=null && ShipStatusEnum.getName(shipStatus)==null){
            return new ResponseVO(-2,"货品状态值异常",null);

        }
        String orderNo=MyEncryptUtil.getRealValue(orderNoEncypt);
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号异常",null);

        }
        Integer orderid=OrderUtil.getOrderId(orderNo);
        Map params=new HashMap();
        if(payStatus!=null){
            params.put("doPayStatus",payStatus);
            if(payStatus==PayStatusEnum.PayStatus1.getCode()){
                params.put("doEndTime",DateUtils.addHours(new Date(),24*7));
                long sum=this.commonSumByBySingleSearchParam("ddw_order_material","materialCountPrice","orderNo",orderNo);

                params.put("doCost",sum);

            }
        }
        if(shipStatus!=null){
            params.put("doShipStatus",shipStatus);
        }
        if(storeId!=null){
            params.put("doCustomerStoreId",storeId);
        }
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",params,"id",orderid);
        if(res.getReCode()!=1){
            return new ResponseVO(-2,"操作失败",null);
        }
        return new ResponseVO(1,"操作成功",null);

    }



}
