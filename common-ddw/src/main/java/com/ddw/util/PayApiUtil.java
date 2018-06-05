package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ddw.beans.RequestAliOrderVO;
import com.ddw.beans.RequestWeiXinOrderVO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.util.HttpUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

public class PayApiUtil {
    private static final Logger logger = Logger.getLogger(PayApiUtil.class);

    /**
     * 微信统一订单接口
     */
    private static String WEIXIN_UNIFIEDORDER="https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 微信统一申请退款
     */
    private static String WEIXIN_REFUND="https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 支付宝支付接口2.0
     */
    private static String ALIPAY_UNIFIEDORDER="https://openapi.alipay.com/gateway.do";

    private static DDWGlobals ddwGlobals;
    static{
       ddwGlobals= Tools.getBean(DDWGlobals.class);
    }
    public static RequestWeiXinOrderVO weiXinOrderQuery(String orderNo){
        //
        Map map=new HashMap();
        map.put("out_trade_no",orderNo);
        String callBackStr= HttpUtil.sendHtpps("https://api.mch.weixin.qq.com/pay/orderquery",wxSign(map));
        if(callBackStr!=null){
            RequestWeiXinOrderVO vo= Tools.xmlCastObject(callBackStr,RequestWeiXinOrderVO.class);
            return vo;
        }
        return null;
    }
    public static RequestWeiXinOrderVO requestWeiXinOrder(String body,String orderNo,Integer cost,String ip)throws Exception{

        Map<String,String> map=new HashMap();
        map.put("body",body);
        map.put("out_trade_no",orderNo);
        map.put("total_fee",cost+"");
        map.put("spbill_create_ip",ip);
        map.put("trade_type","APP");
        map.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/pay/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/pay/execute");

        String callBackStr= HttpUtil.sendHtpps(WEIXIN_UNIFIEDORDER,wxSign(map));
        if(callBackStr!=null){
            RequestWeiXinOrderVO vo= Tools.xmlCastObject(callBackStr,RequestWeiXinOrderVO.class);
            return vo;
        }
        return null;
    }
    public static Map reqeustWeiXinExitOrder(String orderNo,String exitOrderNo,Integer orderCost,Integer exitCost){
        Map map=new HashMap();
        map.put("out_trade_no",orderNo);
        map.put("out_refund_no",exitOrderNo);
        map.put("total_fee",orderCost);
        map.put("refund_fee",exitCost);
        map.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/refund/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/refund/execute");

        String callBackStr= HttpUtil.sendHtpps(WEIXIN_REFUND,wxSign(map));
        Map callMap=Tools.xmlCastMap(callBackStr);
        return callMap;

    }
    public static ResponseVO requestAliExitOrder (String orderNo, Integer exitCost){
        InputStream privateIs=null;
        InputStream publicIs=null;
        try{
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);

            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER,PayApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model=new AlipayTradeRefundModel();
            model.setOutTradeNo(orderNo);
            model.setRefundAmount((double)exitCost/100+"");
            request.setBizModel(model);

            AlipayTradeRefundResponse response= alipayClient.execute(request);
            if(response.isSuccess()){
                return new ResponseVO(1,"成功",null);
            }
        }catch (Exception e){
            logger.error("阿里退款异常",e);

        }finally {
            IOUtils.closeQuietly(privateIs);
            IOUtils.closeQuietly(publicIs);
        }
        return new ResponseVO(-2,"失败",null);

    }
    public static RequestAliOrderVO requestAliPayOrder(String title, String orderNo, String cost, String ip)throws Exception{
        InputStream privateIs=null;
        InputStream publicIs=null;
        try {
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);

            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER,PayApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody(title);
            model.setSubject("支付宝"+title+cost+"元");
            model.setTotalAmount(cost);
            model.setOutTradeNo(orderNo);
            model.setProductCode("QUICK_MSECURITY_PAY");

            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();


            request.setNotifyUrl(URLEncoder.encode(ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/alipay/execute":ddwGlobals.getCallBackHost()+"/manager/alipay/execute","utf-8"));
            request.setBizModel(model);

            AlipayTradeAppPayResponse response =alipayClient.sdkExecute(request);
            if(response.isSuccess()){
                RequestAliOrderVO vo=new RequestAliOrderVO();
                vo.setOrderNo(orderNo);
                vo.setOrderString(response.getBody());
                return vo;
            }

        }catch (Exception e){
            logger.error("阿里支付异常",e);
        }finally {
            IOUtils.closeQuietly(privateIs);
            IOUtils.closeQuietly(publicIs);
        }

        return null;
    }
    public static String wxSign(Map map){
        String nonce_str= RandomStringUtils.randomAlphanumeric(20);
        Document document= DocumentHelper.createDocument();
        Element rootXML=document.addElement("xml");
        TreeMap<String,String> treeMap=new TreeMap(map);
        treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
        treeMap.put("mch_id",PayApiConstant.WEI_XIN_PAY_MCH_ID);
        treeMap.put("nonce_str",nonce_str);
        treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
        Set<String> keys=treeMap.keySet();
        StringBuilder params=new StringBuilder();
        for(String key:keys){
            params.append(key).append("=").append(treeMap.get(key)).append("&");
            rootXML.addElement(key).addCDATA(treeMap.get(key));
        }
        params.append("key=").append(PayApiConstant.WEI_XIN_PAY_KEY);
        rootXML.addElement("sign").addCDATA(DigestUtils.md5Hex(params.toString()).toUpperCase());
        return rootXML.asXML();
    }
    public static void main(String[] args)throws Exception {
       // PayApiUtil.requestAliPayOrder("公用事业","weg","124124124",2000,"0.0.0.0");
      // RequestWeiXinOrderVO vo= PayApiUtil.requestWeiXinOrder("充值","123123123",1,"0.0.0.0");
       // System.out.println(vo);
      // System.out.println((double) 859/100);

        //System.out.println(       requestAliPayOrder(OrderTypeEnum.OrderType3.getName(),"123456789321321","0.01","127.0.0.1"));
        System.out.println(weiXinOrderQuery("01201806052237560301000000000241"));
    }

}
