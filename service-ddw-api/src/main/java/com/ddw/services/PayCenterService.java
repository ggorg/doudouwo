package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.PayApiConstant;
import com.ddw.util.PayApiUtil;
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
        if(OrderTypeEnum.OrderType5.getCode().equals(orderType) || OrderTypeEnum.OrderType4.getCode().equals(orderType)){
            if(cost==null || cost<0){
                return new ResponseApiVO(-2,"金额参数异常，不能小于或者等于0",null);
            }
        }else{
            if(codes==null){
                return new ResponseApiVO(-2,"业务编号不能是空",null);

            }
        }

        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),1));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoCustomerUserId(TokenUtil.getUserId(token));

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
        //定金与竞价金额
        if(OrderTypeEnum.OrderType5.getCode().equals(orderType) || OrderTypeEnum.OrderType4.getCode().equals(orderType)){
            orderPO.setDoSellerId(-1);
            orderPO.setDoCost(cost);
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
            List<Map> goodsPruductList= this.commonObjectsBySearchCondition("ddw_goods_product",search);
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

            orderPO.setDoSellerId(-1);
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
            Map m=new HashMap();
            String orderNo= OrderUtil.createOrderNo(orderPO.getDoOrderDate(),orderType,payType,insertResponseVO.getData());
            ResponseVO<Integer> resVo=null;
            if(orderType.equals(OrderTypeEnum.OrderType3.getCode())){
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",TokenUtil.getUserId(token));
                m.put("dorCost",orderPO.getDoCost());
                m.put("rechargeId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_recharge",m);
            }if(orderType.equals(OrderTypeEnum.OrderType4.getCode()) ||orderType.equals(OrderTypeEnum.OrderType5.getCode())){

                //            CacheUtil.put("appShoppingCart","bidding-pay-"+groupId,payMap);
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",TokenUtil.getUserId(token));
                m.put("dorCost",orderPO.getDoCost());

                Map bidMap=null;
                if(orderType.equals(OrderTypeEnum.OrderType5.getCode())){
                    bidMap=this.biddingService.getCurrentBidMapNoBidEndTime(TokenUtil.getGroupId(token));
                    if(bidMap==null){
                        throw new GenException("没找到当前竞价的记录");
                    }
                    Map<Integer,BiddingPayVO> payMap=(Map) CacheUtil.get("pay","bidding-pay-"+TokenUtil.getGroupId(token));
                    if(payMap==null){
                        throw new GenException("竞价支付超时");
                    }
                    BiddingPayVO vo=payMap.get((Integer) bidMap.get("luckyDogUserId"));
                    if(!orderPO.getDoCost().equals(Integer.parseInt(vo.getNeedPayPrice()))){
                        throw new GenException("支付的金额与竞价的金额不一致");
                    }
                }else{
                    bidMap=this.biddingService.getCurrentBidMap(TokenUtil.getGroupId(token));
                    if(bidMap==null){
                        throw new GenException("支付竞价定金失败，当前竞价已经结束");
                    }
                }

                m.put("biddingId",bidMap.get("id"));
                resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
            }else if(OrderTypeEnum.OrderType1.getCode().equals(orderType)){
                if(buyInProMap==null){
                    throw new GenException("商品支付失败");
                }
                Collection collection=buyInProMap.values();
                Iterator<Map> iterator=collection.iterator();
                Map insertM=null;

                while(iterator.hasNext()){
                    insertM=iterator.next();
                    insertM.put("orderNo",orderNo);
                    insertM.put("orderId",insertResponseVO.getData());
                    resVo=this.commonInsertMap("ddw_order_product",insertM);
                    if(resVo.getReCode()!=1){
                        throw new GenException("商品支付失败");

                    }
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
                insertM.put("goddessUserId",this.liveRadioService.getLiveRadioByGroupId(TokenUtil.getGroupId(token)).getUserid());

                resVo=this.commonInsertMap("ddw_order_gift",insertM);
                if(resVo.getReCode()!=1){
                    throw new GenException("礼物支付失败");

                }
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
                        CacheUtil.put("pay","weixin-pay-"+orderNo,orderType);
                        return new ResponseApiVO(1,"成功",wxVo);
                    }else{
                        throw new GenException("调用微信支付接口失败");
                    }
                } if(PayTypeEnum.PayType2.getCode().equals(payType)){
                    String dcost=(double)orderPO.getDoCost()/100+"";
                    PayCenterAliPayVO alipayVo=new PayCenterAliPayVO();
                    RequestAliOrderVO rvo=PayApiUtil.requestAliPayOrder(OrderTypeEnum.getName(orderType),orderNo,dcost,Tools.getIpAddr());
                    if(rvo==null){
                        throw new GenException("调用支付宝接口失败");

                    }
                    PropertyUtils.copyProperties(alipayVo,rvo);
                    CacheUtil.put("pay","alipay-pay-"+orderNo,orderType);
                    return new ResponseApiVO(1,"成功",alipayVo);
                    // PayApiUtil.requestAliPayOrder("充值","微信充值-"+dcost+"元",orderNo,dcost,Tools.getIpAddr());
                }
            }
        }
        return new ResponseApiVO(-2,"支付失败",null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO exitOrder(List<String> orders)throws Exception{
        if(orders==null){
            return new ResponseApiVO(-2,"参数异常",null);

        }
        OrderPO orderPO=null;
        ExitOrderPO exitOrderPO=null;
        for(String o:orders){
            orderPO=this.commonObjectBySingleParam("ddw_order","id",OrderUtil.getOrderId(o),OrderPO.class);
            exitOrderPO=new ExitOrderPO();
            exitOrderPO.setCreater(orderPO.getDoCustomerUserId());
            exitOrderPO.setCreaterName(orderPO.getCreater());
            exitOrderPO.setCreateTime(new Date());
            exitOrderPO.setExitCost(orderPO.getDoCost());
            exitOrderPO.setTotalCost(orderPO.getDoCost());
            exitOrderPO.setOrderId(orderPO.getId());
            exitOrderPO.setOrderNo(OrderUtil.createOrderNo(orderPO.getDoOrderDate(),orderPO.getDoType(),orderPO.getDoPayType(),orderPO.getId()));
            exitOrderPO.setExitOrderNo(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss")+RandomStringUtils.randomNumeric(6));
            ResponseVO inserRes=this.commonInsert("ddw_exit_order",exitOrderPO);
            if(inserRes.getReCode()!=1){
                throw new GenException("新建退订单失败");
            }
            if(PayTypeEnum.PayType1.getCode().equals(orderPO.getDoPayType())){
                Map<String,String> callMap= PayApiUtil.reqeustWeiXinExitOrder(exitOrderPO.getOrderNo(),exitOrderPO.getExitOrderNo(),orderPO.getDoCost(),orderPO.getDoCost());
                if(callMap!=null && "FAIL".equals(callMap.get("return_code"))){
                    throw new GenException("申请退款失败");
                }else if(callMap!=null && "SUCCESS".equals(callMap.get("return_code")) && "SUCCESS".equals(callMap.get("result_code"))){
                    CacheUtil.put("pay","weixin-refund-"+o,exitOrderPO.getExitOrderNo());
                }

            }else if(PayTypeEnum.PayType2.getCode().equals(orderPO.getDoPayType())){
                ResponseVO resvo=PayApiUtil.requestAliExitOrder(o,orderPO.getDoCost());
                if(resvo.getReCode()==1){
                    Map map=new HashMap();
                    map.put("doPayStatus",PayStatusEnum.PayStatus2.getCode());
                    ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",orderPO.getId());

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
