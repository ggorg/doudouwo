package com.ddw.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ddw.beans.RequestAliOrderVO;
import com.ddw.beans.RequestWeiXinOrderVO;
import com.ddw.config.DDWGlobals;
import com.gen.common.util.HttpUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.InputStream;
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

    private  static void initDdwGlobals(){
        if(ddwGlobals==null){
            try {
                ddwGlobals= Tools.getBean(DDWGlobals.class);
            }catch (Exception e){
                logger.error("init->ddwGlobals",e);
            }
        }
    }
    public static void setDdwGlobals(DDWGlobals d){
            ddwGlobals=d;
    }
    public static ResponseVO aliPayOrderQuery(String orderNo){
        InputStream privateIs=null;
        InputStream publicIs=null;
        try{
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/ali_public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);

            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER, ApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model=new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);

            AlipayTradeQueryResponse response= alipayClient.execute(request);
            if(response.isSuccess()){
                return new ResponseVO(1,"成功",null);
            }
        }catch (Exception e){
            logger.error("查看阿里支付异常",e);

        }finally {
            IOUtils.closeQuietly(privateIs);
            IOUtils.closeQuietly(publicIs);
        }
        return new ResponseVO(-2,"失败",null);
    }
    public static RequestWeiXinOrderVO weiXinOrderQuery(String orderNo)throws Exception{
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
        initDdwGlobals();
        Map<String,String> map=new HashMap();
        map.put("body",body);
        map.put("out_trade_no",orderNo);
        map.put("total_fee",cost+"");
        map.put("spbill_create_ip",ip);
        map.put("trade_type","APP");
        map.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/pay/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/pay/execute");
        map.put("appid", ApiConstant.WEI_XIN_PAY_APP_ID);
        //map.put("mch_id", ApiConstant.WEI_XIN_PAY_MCH_ID);
        String callBackStr= HttpUtil.sendHtpps(WEIXIN_UNIFIEDORDER,wxSign(map));
        if(callBackStr!=null){
            RequestWeiXinOrderVO vo= Tools.xmlCastObject(callBackStr,RequestWeiXinOrderVO.class);
            return vo;
        }
        return null;
    }

    public static RequestWeiXinOrderVO requestWeiXinOrderByJsapi(String body,String orderNo,Integer cost,String ip,String openId)throws Exception{
        initDdwGlobals();
        Map<String,String> map=new HashMap();
        map.put("body",body);
        map.put("out_trade_no",orderNo);
        map.put("total_fee",cost+"");
        map.put("spbill_create_ip",ip);
        map.put("trade_type","JSAPI");
        map.put("openid",openId);


        map.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/pay/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/pay/execute");
        map.put("appid", ApiConstant.WEI_XIN_PUBLIC_APP_ID);

        String callBackStr= HttpUtil.sendHtpps(WEIXIN_UNIFIEDORDER,wxSign(map));
        //String callBackStr= HttpUtil.sendHttpsWithCert(WEIXIN_UNIFIEDORDER,wxSign(map),ddwGlobals.getWxCertPath(), ApiConstant.WEI_XIN_PAY_MCH_ID);
        if(callBackStr!=null){
            RequestWeiXinOrderVO vo= Tools.xmlCastObject(callBackStr,RequestWeiXinOrderVO.class);
            return vo;
        }
        return null;
    }
    public static Map reqeustWeiXinExitOrder(String orderNo,String exitOrderNo,Integer orderCost,Integer exitCost)throws Exception{
        initDdwGlobals();
        Map map=new HashMap();
        map.put("out_trade_no",orderNo);
        map.put("out_refund_no",exitOrderNo);
        map.put("total_fee",orderCost);
        map.put("refund_fee",exitCost);
       // map.put("notify_url",ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/weixin/refund/execute":ddwGlobals.getCallBackHost()+"/manager/weixin/refund/execute");
        String reStr=wxSign(map);
        logger.info("reqeustWeiXinExitOrder->request->"+reStr);
        String callBackStr= HttpUtil.sendHttpsWithCert(WEIXIN_REFUND,reStr,ddwGlobals.getWxCertPath(), ApiConstant.WEI_XIN_PAY_MCH_ID);
        logger.info("reqeustWeiXinExitOrder->response->"+callBackStr);
        Map callMap=Tools.xmlCastMap(callBackStr);
        return callMap;

    }
    public static ResponseVO requestAliExitOrder (String orderNo, Integer exitCost){
        InputStream privateIs=null;
        InputStream publicIs=null;
        try{
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/ali_public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);

            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER, ApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
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

    public static ResponseVO requestAliTransfer(String cost,String no,String accountNo,String accountRealName,String title,String remark){
        initDdwGlobals();
        InputStream privateIs=null;
        InputStream publicIs=null;
        try {
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/ali_public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);
            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER, ApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            AlipayFundTransToaccountTransferModel  model=new AlipayFundTransToaccountTransferModel();
            model.setPayeeType("ALIPAY_LOGONID");
            model.setAmount(cost);
            model.setOutBizNo(no);
            model.setPayeeAccount(accountNo);
            model.setPayeeRealName(accountRealName);
            model.setPayerShowName(title);
            model.setRemark(remark);

            request.setBizModel(model);
            AlipayFundTransToaccountTransferResponse response=alipayClient.execute(request);
            if(response.isSuccess()){

                return new ResponseVO(1,"转账成功",null);
            }
            logger.info("阿里转账->response->"+response.getBody());

        }catch (Exception e){
            logger.error("阿里转账异常",e);

        }
        return new ResponseVO(-2,"转账失败",null);
    }
    public static RequestAliOrderVO requestAliPayOrder(String title, String orderNo, String cost, String ip)throws Exception{
        initDdwGlobals();
        InputStream privateIs=null;
        InputStream publicIs=null;
        try {
            privateIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/private_key");
            publicIs= PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/ali_public_key");
            String privateKey= IOUtils.toString(privateIs);
            String publicKey= IOUtils.toString(publicIs);

            AlipayClient alipayClient=new DefaultAlipayClient(ALIPAY_UNIFIEDORDER, ApiConstant.ALI_PAY_APP_ID,privateKey,"json","utf-8",publicKey,"RSA2");
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody(title);
            model.setSubject("支付宝"+title+cost+"元");
            model.setTotalAmount(cost);
            model.setOutTradeNo(orderNo);
            model.setProductCode("QUICK_MSECURITY_PAY");

            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();


            request.setNotifyUrl(ddwGlobals==null?"http://cnwork.wicp.net:40431/manager/alipay/execute":ddwGlobals.getCallBackHost()+"/manager/alipay/execute");
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
    public static String wxSign(Map map)throws Exception{
        String nonce_str= RandomStringUtils.randomAlphanumeric(20);
        Document document= DocumentHelper.createDocument();
        Element rootXML=document.addElement("xml");
        TreeMap treeMap=new TreeMap(map);

        treeMap.put("nonce_str",nonce_str);
        treeMap.put("sign_type","MD5");
        treeMap.put("mch_id", ApiConstant.WEI_XIN_PAY_MCH_ID);
        Set<String> keys=treeMap.keySet();
        StringBuilder params=new StringBuilder();
        for(String key:keys){
            params.append(key).append("=").append(treeMap.get(key)).append("&");
            rootXML.addElement(key).addText(treeMap.get(key).toString());
        }
        params.append("key=").append(ApiConstant.WEI_XIN_PAY_KEY);

        rootXML.addElement("sign").addText(DigestUtils.md5Hex(params.toString()).toUpperCase());
        //rootXML.addElement("sign").addText(WXPayUtil.HMACSHA256(params.toString(),ApiConstant.WEI_XIN_PAY_KEY).toUpperCase());
        logger.info(rootXML.asXML()+","+params);
        return rootXML.asXML();
    }
    public static void main(String[] args)throws Exception {
       // PayApiUtil.requestAliPayOrder("公用事业","weg","124124124",2000,"0.0.0.0");
        Properties pro=new Properties();
        pro.load(tls_sigcheck.class.getClassLoader().getResourceAsStream("application-dev.properties"));
       ddwGlobals=new DDWGlobals();
       ddwGlobals.setWxCertPath(pro.getProperty("wx.cert.path"));
        setDdwGlobals(ddwGlobals);
       RequestWeiXinOrderVO vo= PayApiUtil.requestWeiXinOrder("充值","12312312938",1,"0.0.0.0");
        System.out.println(vo);
      // System.out.println((double) 859/100);

        //System.out.println(       requestAliPayOrder(OrderTypeEnum.OrderType3.getName(),"123456789321321","0.01","127.0.0.1"));
        //System.out.println(aliPayOrderQuery("01201806061256390302000000000257"));
        //System.out.println(        requestAliExitOrder("01201806061256390302000000000257",1));
        //System.out.println(reqeustWeiXinExitOrder("01201806061250150301000000000256",RandomStringUtils.randomAlphabetic(20),1,1));

        System.out.println(requestWeiXinOrderByJsapi("test","123123129386",1,"127.0.0.1","oGb9J03naSTqAXJWLvWIYafTRvts"));


    }


}
