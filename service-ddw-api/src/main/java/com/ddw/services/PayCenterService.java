package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.PayApiConstant;
import com.ddw.util.PayApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PayCenterService extends BaseOrderService {
    private final Logger logger = Logger.getLogger(PayCenterService.class);

    @Autowired
    private BiddingService biddingService;


    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private LiveRadioService liveRadioService;


    @Autowired
    private DDWGlobals ddwGlobals;
    public ResponseApiVO searchPayStatus(String token,PayStatusDTO dto)throws Exception{
        if(dto==null || StringUtils.isBlank(dto.getOrderNo())){
            return new ResponseApiVO(-2,"参数异常",null);

        }
        String paystatus=null;
        for(int i=1;i<=4;i++){
            paystatus=(String)CacheUtil.get("pay","order-"+dto.getOrderNo());
            if(paystatus==null){
                Thread.sleep(i*200);
                continue;
            }else{
                break;
            }
        }
        if(paystatus==null){
            Map map=new HashMap();
            if(CacheUtil.get("pay","weixin-pay-"+dto.getOrderNo()) ==null && CacheUtil.get("pay","alipay-pay-"+dto.getOrderNo())==null){
                return new ResponseApiVO(-2,"抱歉，没有支付记录",null);

            }
            map.put("doCustomerUserId",TokenUtil.getUserId(token));
            map.put("id",OrderUtil.getOrderId(dto.getOrderNo()));
            Map voMap=this.commonObjectBySearchCondition("ddw_order",map);
            if(voMap==null || !voMap.containsKey("doPayStatus")){
                return new ResponseApiVO(-2,"支付记录不存在",null);

            }
            /*Date endTime=(Date)voMap.get("doEndTime");
            if(endTime.before(new Date())){
                return new ResponseApiVO(-3,"支付失败，已超时",null);
            }*/
            Integer doPayStatus=(Integer) voMap.get("doPayStatus");
            if(PayStatusEnum.PayStatus1.getCode().equals(doPayStatus)){
                return new ResponseApiVO(1,"支付成功",null);

            }else{
               Integer payType=(Integer) voMap.get("doPayType");
               boolean flag=false;
               if(PayTypeEnum.PayType1.getCode().equals(payType)){
                   logger.info("请求微信支付-》查看订单情况->"+dto);
                   RequestWeiXinOrderVO res= PayApiUtil.weiXinOrderQuery(dto.getOrderNo());
                   logger.info("微信支付响应-》查看订单情况->"+res);
                   flag=res!=null && "SUCCESS".equals(res.getReturn_code()) && "SUCCESS".equals(res.getResult_code());

               }else if(PayTypeEnum.PayType2.getCode().equals(payType)){
                   logger.info("请求阿里支付-》查看订单情况->"+dto);
                   ResponseVO res= PayApiUtil.aliPayOrderQuery(dto.getOrderNo());
                   logger.info("阿里支付响应-》查看订单情况->"+res);
                   flag=res!=null && (res.getReCode()==1 || res.getReCode().equals(1));
               }
               if(flag){
                   Map param=new HashMap();
                   param.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
                   ResponseVO orderRes=this.pulbicUpdateOrderPayStatus(PayStatusEnum.PayStatus1,dto.getOrderNo());
                   if(orderRes.getReCode()!=1){
                       logger.info("更新订单表-》失败->"+orderRes);
                   }else{
                       logger.info("更新订单表-》成功->"+orderRes);

                   }
                   return new ResponseApiVO(1,"支付成功",null);
               }

            }

        }else if("success".equals(paystatus)){
            return new ResponseApiVO(1,"支付成功",null);

        }else if("fail".equals(paystatus)){
            return new ResponseApiVO(-3,"支付失败",null);

        }
        return new ResponseApiVO(-4,"支付处理中，请稍等",null);

    }
    /**
     * 预支付
     * @param token
     * @param cost
     * @param payType
     * @param orderType
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO prePay(String token, Integer cost, Integer payType, Integer orderType,Integer[] codes)throws Exception{

        if(!PayTypeEnum.PayType1.getCode().equals(payType) && !PayTypeEnum.PayType2.getCode().equals(payType)){
            return new ResponseApiVO(-2,"请选择有效的支付方式",null);
        }
        if(StringUtils.isBlank(OrderTypeEnum.getName(orderType))){
            return new ResponseApiVO(-2,"请选择有效的订单类型",null);

        }

        if(codes==null){
            return new ResponseApiVO(-2,"业务编号不能是空",null);

        }
        Integer userId=TokenUtil.getUserId(token);

        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),1));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoCustomerUserId(userId);

        orderPO.setDoCustomerStoreId(-1);
        orderPO.setDoPayType(payType);
        orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());
        orderPO.setDoCustomerType(OrderCustomerTypeEnum.OrderCustomerType0.getCode());
        orderPO.setDoType(orderType);
        orderPO.setCreater(TokenUtil.getUserName(token));
        Map<Integer,Map> buyInProMap=null;
        //GiftPO gift=null;
        Map voData=null;
        List<Map> insertList=null;
        //定金
        if(OrderTypeEnum.OrderType4.getCode().equals(orderType)){
            orderPO.setDoSellerId(-1);
            Integer earnest=(Integer)CacheUtil.get("pay","bidding-earnest-pay-"+userId+"-"+codes[0]);
            if(earnest==null){
                return new ResponseApiVO(-2,"定金支付失败",null);
            }
            orderPO.setDoCost(earnest);
       // 竞价金额
        }else if(OrderTypeEnum.OrderType5.getCode().equals(orderType)){
            orderPO.setDoSellerId(-1);
            Integer bidCost=0;
            Map bidMap=null;
            for(Integer code:codes){
                bidMap=(Map)CacheUtil.get("pay","bidding-pay-"+userId+"-"+codes[0]);
                if(bidMap==null){
                    return new ResponseApiVO(-2,"竞价金额支付失败",null);

                }else{
                    bidCost=bidCost+Integer.parseInt((String) bidMap.get("needPayPrice"));
                }

            }
            orderPO.setDoCost(bidCost);
        //计算充值
        }else if(OrderTypeEnum.OrderType3.getCode().equals(orderType)){
            orderPO.setDoSellerId(-1);
            orderPO.setDoCost(rechargeService.getRechargeCost(codes[0]));
            if(orderPO.getDoCost()==null){
                return new ResponseApiVO(-2,"充值卷编号异常",null);
            }
        //计算货品
        }else if(OrderTypeEnum.OrderType1.getCode().equals(orderType)){
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));

            buyInProMap=new HashMap();
            List<Integer> codesList=Arrays.asList(codes);
            Map search=new HashMap();
            search.put("storeId",TokenUtil.getStoreId(token));
            search.put("dghStatus",GoodsStatusEnum.goodsStatus1.getCode());
            search.put("id,in",codesList.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
            CommonSearchBean csb=new CommonSearchBean("ddw_goods_product",null,"t1.dghActivityPrice,t1.dghSalesPrice,t1.dghDesc,t1.id,ct0.fileImgIcoPath headImg",null,null,search,new CommonChildBean("ddw_goods","id","dghGoodsId",null));
            List<Map> goodsPruductList=this.getCommonMapper().selectObjects(csb);
           // List<Map> goodsPruductList= this.commonObjectsBySearchCondition("ddw_goods_product",search);
            if(goodsPruductList==null || goodsPruductList.isEmpty()){
                return new ResponseApiVO(-2,"货品不存在",null);
            }
            Map<Integer,Map> handleMap=new HashMap();
            goodsPruductList.forEach(a->{handleMap.put((Integer) a.get("id"),a);});

            Map dataMap=null;
            Integer countPrice=0;
            Map mVo=null;
            for(Integer code:codesList){
                if(!handleMap.containsKey(code)){
                    return new ResponseApiVO(-2,handleMap.get(code).get("dghDesc")+"可能已下架",null);
                }
                if(buyInProMap.containsKey(code)){
                    dataMap=buyInProMap.get(code);
                    Integer sale=(Integer) dataMap.get("productUnitPrice");
                    countPrice=countPrice+sale;
                    dataMap.put("productCountPrice",(Integer)dataMap.get("productCountPrice")+sale);
                    dataMap.put("productBuyNumber",(Integer)dataMap.get("productBuyNumber")+1);
                }else{
                    mVo=handleMap.get(code);
                    dataMap=new HashMap();
                    dataMap.put("productId",code);
                    Integer sale=mVo.get("dghActivityPrice")==null?(Integer)mVo.get("dghSalesPrice"):(Integer)mVo.get("dghActivityPrice");
                    countPrice=countPrice+sale;
                    dataMap.put("productCountPrice",sale);
                    dataMap.put("productUnitPrice",sale);
                    dataMap.put("productBuyNumber",1);
                    dataMap.put("updateTime",new Date());
                    dataMap.put("createTime",new Date());
                    dataMap.put("productName",mVo.get("dghDesc"));
                    dataMap.put("headImg",mVo.get("headImg"));
                    buyInProMap.put(code,dataMap);
                }

            }
           orderPO.setDoCost(countPrice);
            if(countPrice==null || countPrice<=0){
                return new ResponseApiVO(-2,"支付失败",null);
            }
        //礼物
        }else if(OrderTypeEnum.OrderType6.getCode().equals(orderType)){
            orderPO.setDoSellerId(-1);
            Map search=new HashMap();
            search.put("id",codes[0]);
            search.put("dgDisabled",DisabledEnum.disabled0.getCode());
            voData=this.commonObjectBySearchCondition("ddw_gift",search);
            if(voData==null){
                return new ResponseApiVO(-2,"礼物不存在",null);
            }
            Integer actPrice=(Integer)voData.get("dgActPrice");
            Integer price=(Integer)voData.get("dgPrice");
            orderPO.setDoCost(actPrice!=null && actPrice>0?actPrice:price);
        }else if(OrderTypeEnum.OrderType7.getCode().equals(orderType)){

            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            List<Integer> codesList=Arrays.asList(codes);
            Map search=new HashMap();
            search.put("dtDisabled",DisabledEnum.disabled0.getCode());
            search.put("id,in",codesList.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
            List<Map> tocketList=this.commonObjectsBySearchCondition("ddw_ticket",search);
            if(tocketList==null){
                return new ResponseApiVO(-2,"门票不存在",null);
            }
            final Map<Integer,Map> buyInTicketMap=new HashMap();
            for(Map vd:tocketList){
                buyInTicketMap.put((Integer) vd.get("id"),vd);
            }
            insertList=new ArrayList();
            Map orderTicket=null;
            Map ticketMap=null;
            Integer sumPrice=0;
            Integer price=0;
            for(Integer id:codesList){
                orderTicket=new HashMap();
                ticketMap=buyInTicketMap.get(id);
                price= ticketMap.get("dtActPrice")==null?(Integer)ticketMap.get("dtPrice"):(Integer)ticketMap.get("dtActPrice");
                sumPrice=sumPrice+price;
                orderTicket.put("ticketId",id);
                orderTicket.put("storeId",TokenUtil.getStoreId(token));
                orderTicket.put("ticketName",ticketMap.get("dtName"));
                orderTicket.put("ticketPrice",price);
                insertList.add(orderTicket);
            }
            orderPO.setDoCost(sumPrice);


        }

        ResponseVO<Integer> insertResponseVO=this.commonInsert("ddw_order",orderPO);

        if(insertResponseVO.getReCode()==1){
            Object orderCacheData=orderType;
            String orderNo= OrderUtil.createOrderNo(orderPO.getDoOrderDate(),orderType,payType,insertResponseVO.getData());
            ResponseVO<Integer> resVo=null;
            if(orderType.equals(OrderTypeEnum.OrderType3.getCode())){
                Map m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dorCost",orderPO.getDoCost());
                m.put("rechargeId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_recharge",m);
            }else if(orderType.equals(OrderTypeEnum.OrderType4.getCode()) ){
                String ub=userId+"-"+codes[0];
                Integer earnest=(Integer)CacheUtil.get("pay","bidding-earnest-pay-"+ub);
                if(earnest==null){
                    throw new GenException("定金支付超时");

                }
                Map m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dorCost",earnest);
                m.put("biddingId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
                orderCacheData=ub;
            }else if(orderType.equals(OrderTypeEnum.OrderType5.getCode())){
                Map payMap=null;
                Map m=null;
                List list=new ArrayList();
                String ub=null;
                for(Integer code:codes){
                    ub=userId+"-"+code;
                    payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
                    if(payMap==null){
                        throw new GenException("竞价金额支付超时");
                    }
                    m=new HashMap();
                    m.put("orderId",insertResponseVO.getData());
                    m.put("orderNo",orderNo);
                    m.put("createTime",new Date());
                    m.put("updateTime",new Date());
                    m.put("creater",userId);
                    m.put("dorCost",payMap.get("needPayPrice"));
                    m.put("biddingId",code);
                    resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
                    if(resVo.getReCode()!=1){
                        throw new GenException("竞价金额支付失败");

                    }
                    list.add(ub);

                }
                orderCacheData=list;

            }else if(OrderTypeEnum.OrderType1.getCode().equals(orderType)){
                if(buyInProMap==null){
                    throw new GenException("商品支付失败");
                }
                Collection collection=buyInProMap.values();
                Iterator<Map> iterator=collection.iterator();
                Map insertM=null;
                String headImg=null;
                while(iterator.hasNext()){
                    insertM=iterator.next();
                    insertM.put("orderNo",orderNo);
                    insertM.put("orderId",insertResponseVO.getData());
                    headImg=(String)insertM.get("headImg");
                    insertM.remove("headImg");
                    resVo=this.commonInsertMap("ddw_order_product",insertM);
                    if(resVo.getReCode()!=1){
                        throw new GenException("商品支付失败");

                    }
                    insertM.put("headImg",headImg);
                }
                CacheUtil.put("pay","goodsPru-order-"+orderNo,new ArrayList(collection));
            }else if(OrderTypeEnum.OrderType6.getCode().equals(orderType)){
                Map insertM=new HashMap();
                insertM.put("orderNo",orderNo);
                insertM.put("orderId",insertResponseVO.getData());
                insertM.put("createTime",new Date());
                insertM.put("updateTime",new Date());
                insertM.put("giftId",voData.get("id"));
                insertM.put("giftName",voData.get("dgName").toString());
                insertM.put("giftPrice",orderPO.getDoCost());
                Integer goddessUserId=this.liveRadioService.getLiveRadioByGroupId(TokenUtil.getGroupId(token)).getUserid();
                insertM.put("goddessUserId",goddessUserId);

                resVo=this.commonInsertMap("ddw_order_gift",insertM);
                if(resVo.getReCode()!=1){
                    throw new GenException("礼物支付失败");

                }
                Map map=new HashMap();
                map.put("goddessUserId",goddessUserId);
                map.put("cost",orderPO.getDoCost());
                map.put("headImg",voData.get("dgImgPath"));
                map.put("name",voData.get("dgName").toString());
                orderCacheData=JSONObject.toJSONString(map);
            }else if(OrderTypeEnum.OrderType7.getCode().equals(orderType)){
                for(Map im:insertList){
                    im.put("orderId",insertResponseVO.getData());
                    im.put("orderNo",orderNo);
                    im.put("createTime",new Date());
                    im.put("updateTime",new Date());
                    resVo=this.commonInsertMap("ddw_order_ticket",im);
                    if(resVo.getReCode()!=1){
                        throw new GenException("门票支付失败");
                    }

                }
            }

            if(resVo.getReCode()==1){
                if(PayTypeEnum.PayType1.getCode().equals(payType)){
                    /*RequestWeiXinOrderVO vo=new RequestWeiXinOrderVO();
                    vo.setReturn_code("SUCCESS");
                    vo.setResult_code("SUCCESS");
                    vo.setPrepay_id(RandomStringUtils.randomAlphabetic(10));*/
                    RequestWeiXinOrderVO vo=PayApiUtil.requestWeiXinOrder("微信"+OrderTypeEnum.getName(orderType)+"-"+((double)orderPO.getDoCost()/100)+"元",orderNo,orderPO.getDoCost(), Tools.getIpAddr());
                    if(vo!=null && "SUCCESS".equals(vo.getReturn_code()) && "SUCCESS".equals(vo.getResult_code())){
                        TreeMap treeMap=new TreeMap();
                        treeMap.put("appid", PayApiConstant.WEI_XIN_PAY_APP_ID);
                        treeMap.put("partnerid",PayApiConstant.WEI_XIN_PAY_MCH_ID);
                        treeMap.put("prepayid",vo.getPrepay_id());
                        treeMap.put("package","Sign=WXPay");
                        treeMap.put("noncestr", RandomStringUtils.randomAlphanumeric(20)+"");
                        treeMap.put("timestamp",new Date().getTime()/1000+"");
                        Set<String> keys=treeMap.keySet();
                        StringBuilder builder=new StringBuilder();
                        for(String key:keys){
                            builder.append(key).append("=").append(treeMap.get(key)).append("&");
                        }
                        builder.append("key=").append(PayApiConstant.WEI_XIN_PAY_KEY);
                       // builder.deleteCharAt(builder.length()-1);
                        treeMap.put("sign", DigestUtils.md5Hex(builder.toString()).toUpperCase());
                        treeMap.put("packages",treeMap.get("package"));
                        treeMap.remove("package");
                        PayCenterWeixinPayVO wxVo=new PayCenterWeixinPayVO();
                        PropertyUtils.copyProperties(wxVo,treeMap);
                        wxVo.setOrderNo(orderNo);
                        CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                        CacheUtil.put("pay",".orderObject-"+orderNo,JSONObject.toJSONString(orderPO));
                        return new ResponseApiVO(1,"成功",wxVo);
                    }else{
                        throw new GenException("调用微信支付接口失败");
                    }
                }else if(PayTypeEnum.PayType2.getCode().equals(payType)){
                    String dcost=(double)orderPO.getDoCost()/100+"";
                    PayCenterAliPayVO alipayVo=new PayCenterAliPayVO();
                    RequestAliOrderVO rvo=PayApiUtil.requestAliPayOrder(OrderTypeEnum.getName(orderType),orderNo,dcost,Tools.getIpAddr());
                    if(rvo==null){
                        throw new GenException("调用支付宝接口失败");

                    }
                    PropertyUtils.copyProperties(alipayVo,rvo);
                    CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                    CacheUtil.put("pay","orderObject-"+orderNo,JSONObject.toJSONString(orderPO));

                    return new ResponseApiVO(1,"成功",alipayVo);
                    // PayApiUtil.requestAliPayOrder("充值","微信充值-"+dcost+"元",orderNo,dcost,Tools.getIpAddr());
                }
            }else{
                throw new GenException("支付失败");

            }
        }
        return new ResponseApiVO(-2,"支付失败",null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO exitOrder(List<Integer> orderIds)throws Exception{
        if(orderIds==null){
            return new ResponseApiVO(-2,"参数异常",null);

        }else if(orderIds.isEmpty()){
            return new ResponseApiVO(1,"成功",null);

        }
        ExitOrderPO exitOrderPO=null;
        Map searchMap=new HashMap();
        searchMap.put("id,in",orderIds.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
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
            exitOrderPO.setOrderNo(OrderUtil.createOrderNo((String)o.get("doOrderDate"),(Integer) o.get("doType"),(Integer) o.get("doPayType"),exitOrderPO.getOrderId()));
            exitOrderPO.setExitOrderNo(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss")+RandomStringUtils.randomNumeric(6));
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

        return new ResponseApiVO(1,"成功",null);
    }

    public static void main(String[] args) {
        Integer[] id={1,2,3,4};
        System.out.println(Arrays.asList(id).toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
    }
}
