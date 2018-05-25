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
public class PayCenterService extends CommonService {
    private final Logger logger = Logger.getLogger(PayCenterService.class);

    @Autowired
    private BiddingService biddingService;



    @Autowired
    private DDWGlobals ddwGlobals;
    public ResponseApiVO searchPayStatus(String token,PayStatusDTO dto)throws Exception{
        if(dto==null || StringUtils.isBlank(dto.getOrderNo())){
            return new ResponseApiVO(-2,"参数异常",null);

        }
        String paystatus=null;
        for(int i=1;i<=3;i++){
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
            Date endTime=(Date)voMap.get("doEndTime");
            if(endTime.before(new Date())){
                return new ResponseApiVO(-3,"支付失败，已超时",null);
            }
            Integer doPayStatus=(Integer) voMap.get("doPayStatus");
            if(PayStatusEnum.PayStatus1.getCode().equals(doPayStatus)){
                return new ResponseApiVO(1,"支付成功",null);

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
    public ResponseApiVO prePay(String token, Integer cost, Integer payType, Integer orderType)throws Exception{

        if(!PayTypeEnum.PayType1.getCode().equals(payType) && !PayTypeEnum.PayType2.getCode().equals(payType)){
            return new ResponseApiVO(-2,"请选择有效的支付方式",null);
        }
        if(StringUtils.isBlank(OrderTypeEnum.getName(orderType))){
            return new ResponseApiVO(-2,"请选择有效的订单类型",null);

        }
        if(cost==null || cost<0){
            return new ResponseApiVO(-2,"金额参数异常，不能小于或者等于0",null);

        }
        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),1));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoCustomerUserId(TokenUtil.getUserId(token));
        orderPO.setDoSellerId(-1);
        orderPO.setDoCustomerStoreId(-1);
        orderPO.setDoPayType(payType);
        orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());
        orderPO.setDoCustomerType(OrderCustomerTypeEnum.OrderCustomerType0.getCode());
        orderPO.setDoType(orderType);
        orderPO.setCreater(TokenUtil.getUserName(token));
        orderPO.setDoCost(cost);
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
                m.put("dorCost",cost);
                resVo=this.commonInsertMap("ddw_order_recharge",m);
            }if(orderType.equals(OrderTypeEnum.OrderType4.getCode()) ||orderType.equals(OrderTypeEnum.OrderType5.getCode())){

                //            CacheUtil.put("appShoppingCart","bidding-pay-"+groupId,payMap);
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",TokenUtil.getUserId(token));
                m.put("dorCost",cost);
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
                    if(!cost.equals(Integer.parseInt(vo.getNeedPayPrice()))){
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
            }

            if(resVo.getReCode()==1){
                if(PayTypeEnum.PayType1.getCode().equals(payType)){
                    /*RequestWeiXinOrderVO vo=new RequestWeiXinOrderVO();
                    vo.setReturn_code("SUCCESS");
                    vo.setResult_code("SUCCESS");
                    vo.setPrepay_id(RandomStringUtils.randomAlphabetic(10));*/
                    RequestWeiXinOrderVO vo=PayApiUtil.requestWeiXinOrder("微信"+OrderTypeEnum.getName(orderType)+"-"+((double)cost/100)+"元",orderNo,cost, Tools.getIpAddr());
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
                    String dcost=(double)cost/100+"";
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
}
