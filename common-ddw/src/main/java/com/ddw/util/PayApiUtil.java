package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.ddw.beans.RequestWeiXinOrderVO;
import com.ddw.config.DDWGlobals;
import com.gen.common.util.HttpUtil;
import com.gen.common.util.Tools;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

public class PayApiUtil {

    /**
     * 微信统一订单接口
     */
    private static String WEIXIN_UNIFIEDORDER="https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 支付宝支付接口2.0
     */
    private static String ALIPAY_UNIFIEDORDER="https://openapi.alipay.com/gateway.do";

    private static DDWGlobals ddwGlobals;
    static{
       ddwGlobals= Tools.getBean(DDWGlobals.class);
    }

    public static RequestWeiXinOrderVO requestWeiXinOrder(String body,String orderNo,Integer cost,String ip)throws Exception{
        String nonce_str= RandomStringUtils.randomAlphanumeric(20);
        Document document= DocumentHelper.createDocument();
        Element rootXML=document.addElement("xml");
        TreeMap<String,String> treeMap=new TreeMap();
        treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
        treeMap.put("mch_id",PayApiConstant.WEI_XIN_PAY_MCH_ID);
        treeMap.put("nonce_str",nonce_str);
        treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
        treeMap.put("body",body);
        treeMap.put("out_trade_no",orderNo);
        treeMap.put("total_fee",cost+"");
        treeMap.put("spbill_create_ip",ip);
        treeMap.put("trade_type","APP");
        treeMap.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/pay/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/pay/execute");
        Set<String> keys=treeMap.keySet();
        StringBuilder params=new StringBuilder();
        for(String key:keys){
            params.append(key).append("=").append(treeMap.get(key)).append("&");
            rootXML.addElement(key).addCDATA(treeMap.get(key));
        }
        params.append("key=").append(PayApiConstant.WEI_XIN_PAY_KEY);
        rootXML.addElement("sign").addCDATA(DigestUtils.md5Hex(params.toString()).toUpperCase());
        String callBackStr= HttpUtil.sendHtpps(WEIXIN_UNIFIEDORDER,rootXML.asXML());
        if(callBackStr!=null){
            RequestWeiXinOrderVO vo= Tools.xmlCastObject(callBackStr,RequestWeiXinOrderVO.class);
            return vo;
        }
        return null;
    }
    public static void requestAliPayOrder(String subject,String body,String orderNo,String cost,String ip)throws Exception{
        //String nonce_str= RandomStringUtils.randomAlphanumeric(20);

        /*StringBuilder builder=new StringBuilder();
        builder.append(ALIPAY_UNIFIEDORDER).append("?");
        builder.append("app_id=").append(PayApiConstant.ALI_PAY_APP_ID).append("&");
        builder.append("method=").append("alipay.trade.app.pay").append("&");
        builder.append("charset=").append("utf-8").append("&");
        builder.append("sign_type=").append("RSA2").append("&");
        builder.append("sign=").append("").append("&");
        builder.append("timestamp=").append(DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss")).append("&");
        builder.append("version=2.0&");
        builder.append("notify_url=http://cnwork.wicp.net:40431/manager/alipay/execut&");
        //builder.append("notify_url=").append(ddwGlobals.getCallBackHost()+"/manager/alipay/execute").append("&");
        Map data=new HashMap();
        data.put("total_amount", (double)cost/100);
        data.put("subject", subject);
        data.put("body", body);
        data.put("out_trade_no", orderNo);
        builder.append("biz_content=").append(JSONObject.toJSONString(data));
        String callBackStr= HttpUtil.sendHtpps(builder.toString(),null);
        System.out.println(callBackStr);*/
        String privateKey= IOUtils.toString(PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key"));
        String publicKey= IOUtils.toString(PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/public_key"));
        System.out.println(privateKey);
        System.out.println(publicKey);
        AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER,PayApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(body);
        model.setSubject(subject);
        model.setTotalAmount( cost);
        model.setOutTradeNo(orderNo);


        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        Map data=new HashMap();
        data.put("total_amount", cost);
        data.put("subject", subject);
        data.put("body", body);
        data.put("out_trade_no", orderNo);
        request.setBizContent(JSONObject.toJSONString(data));

        request.setNotifyUrl(URLEncoder.encode(ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/alipay/execute":ddwGlobals.getCallBackHost()+"/manager/alipay/execute","utf-8"));
        request.setBizModel(model);
        AlipayTradeAppPayResponse response =alipayClient.execute(request);
        System.out.println(response.isSuccess());
       // System.out.println(response.getCode()+","+response.getMsg());
        // data.put("notify_url", ddwGlobals.getCallBackHost()+"/manager/alipay/execute");
       // String callBackStr= HttpUtil.sendHtpps(WEIXIN_UNIFIEDORDER,rootXML.asXML());
    }

    public static void main(String[] args)throws Exception {
       // PayApiUtil.requestAliPayOrder("公用事业","weg","124124124",2000,"0.0.0.0");
      // RequestWeiXinOrderVO vo= PayApiUtil.requestWeiXinOrder("充值","123123123",1,"0.0.0.0");
       // System.out.println(vo);
      // System.out.println((double) 859/100);

        XStream xStream=new XStream();
        xStream.alias("xml",RequestWeiXinOrderVO.class);
       // xStream.processAnnotations(RequestWeiXinOrderVO.class);
        RequestWeiXinOrderVO vo= (RequestWeiXinOrderVO)xStream.fromXML("<xml><return_code>wegweg</return_code></xml>");
        System.out.println(vo);

    }

}
