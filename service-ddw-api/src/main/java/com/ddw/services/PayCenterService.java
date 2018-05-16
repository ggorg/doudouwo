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
        ResponseVO<Integer> insertResponseVO=this.commonInsert("ddw_order",orderPO);
        if(insertResponseVO.getReCode()==1){
            Map m=new HashMap();
            String orderNo= OrderUtil.createOrderNo(orderPO.getDoOrderDate(),OrderTypeEnum.OrderType3.getCode(),payType,insertResponseVO.getData());
            ResponseVO<Integer> resVo=null;
            if(orderType.equals(OrderTypeEnum.OrderType3.getCode())){
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",TokenUtil.getUserId(token));
                m.put("dorCost",cost);
                resVo=this.commonInsertMap("ddw_order_recharge",m);
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
                        builder.deleteCharAt(builder.length()-1);
                        treeMap.put("sign", DigestUtils.md5Hex(builder.toString()).toUpperCase());
                        treeMap.put("packages",treeMap.get("package"));
                        treeMap.remove("package");
                        WalletWeixinRechargeVO wxVo=new WalletWeixinRechargeVO();
                        PropertyUtils.copyProperties(wxVo,treeMap);
                        wxVo.setOrderNo(orderNo);
                        CacheUtil.put("pay","weixin-pay-"+orderNo,OrderTypeEnum.OrderType3.getCode());
                        return new ResponseApiVO(1,"成功",wxVo);
                    }else{
                        throw new GenException("调用微信支付接口失败");
                    }
                } if(PayTypeEnum.PayType2.getCode().equals(payType)){
                    String dcost=(double)cost/100+"";
                    Map data=new HashMap();
                    data.put("total_amount", cost+"");
                    data.put("subject", OrderTypeEnum.getName(orderType));
                    data.put("body", "支付宝"+OrderTypeEnum.getName(orderType)+dcost+"元");
                    data.put("out_trade_no", orderNo);
                    data.put("product_code", "QUICK_MSECURITY_PAY");
                    WalletAlipayRechargeVO alipayVo=new WalletAlipayRechargeVO();
                    alipayVo.setApp_id(PayApiConstant.ALI_PAY_APP_ID);
                    alipayVo.setBiz_content(JSONObject.toJSONString(data));
                    alipayVo.setCharset("utf-8");
                    alipayVo.setMethod("alipay.trade.app.pay");
                    alipayVo.setNotify_url(ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/alipay/execute":ddwGlobals.getCallBackHost()+"/manager/alipay/execute");
                    alipayVo.setSign_type("RSA2");
                    alipayVo.setTimestamp(DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
                    alipayVo.setVersion("1.0");
                    Map map= BeanToMapUtil.beanToMap(alipayVo,true);
                    map.remove("orderNo");
                    TreeMap treeMap=new TreeMap(map);
                    Set<String> keys=treeMap.keySet();
                    StringBuilder builder=new StringBuilder();
                    for(String k:keys){
                        builder.append(k).append("=").append(treeMap.get(k)).append("&");
                    }

                    builder.deleteCharAt(builder.length()-1);
                    InputStream is=PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
                    String privateKey= IOUtils.toString(is);
                    IOUtils.closeQuietly(is);
                    treeMap.clear();
                    alipayVo.setSign(AlipaySignature.rsa256Sign(builder.toString(),privateKey,"utf-8"));
                    CacheUtil.put("pay","alipay-pay-"+orderNo,OrderTypeEnum.OrderType3.getCode());
                    alipayVo.setOrderNo(orderNo);
                    return new ResponseApiVO(1,"成功",alipayVo);
                    // PayApiUtil.requestAliPayOrder("充值","微信充值-"+dcost+"元",orderNo,dcost,Tools.getIpAddr());
                }
            }
        }
        return new ResponseApiVO(-2,"充值失败",null);
    }
}
