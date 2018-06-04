package com.ddw.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.ddw.beans.*;
import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.services.LiveRadioService;
import com.ddw.servies.AsyncService;
import com.ddw.servies.OrderService;
import com.ddw.util.SignUtil;
import com.gen.common.services.CacheService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.HttpUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/manager")
public class CallBackController {
    private final Logger logger = Logger.getLogger(CallBackController.class);


    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private OrderService orderService;

    @Value("${ProxyCallBackHost:}")
    private String proxyCallBackHost;

    @Value("${pay.sign.close:}")
    private String paySignClose;
    /**
     1	recv rtmp deleteStream	主播端主动断流
     2	recv rtmp closeStream	主播端主动断流
     3	recv() return 0	主播端主动断开 TCP 连接
     4	recv() return error	主播端 TCP 连接异常
     7	rtmp message large than 1M	收到流数据异常
     18	push url maybe invalid	推流鉴权失败，服务端禁止推流
     19	3rdparty auth failed	第三方鉴权失败，服务端禁止推流
     * @param dto
     * @return
             */
    @PostMapping("/live/execute")
    @ResponseBody
    public String liveExecute( @RequestBody LiveRadioCallBackDTO dto){
        try {
            logger.info("liveExecute-request："+dto);
            if(proxyCallBackHost!=null){
                asyncService.requestProxyHost(proxyCallBackHost+"/manager/live/execute", JSON.toJSONString(dto));
            }
            if(dto.getEvent_type().equals(LiveEventTypeEnum.eventType1.getCode())){
                ResponseVO responseVO=this.liveRadioService.handleLiveRadioStatus(dto.getStream_id(),dto.getEvent_type());
                if(responseVO.getReCode()==1){

                    return "{ \"code\":0 }";
                }
            }else if(dto.getEvent_type().equals(LiveEventTypeEnum.eventType0.getCode())){
                logger.info("publicCache:"+CacheUtil.get("publicCache","closeCmd-"+dto.getStream_id()));

                return "{ \"code\":0 }";
            }

        }catch (Exception e){
            logger.error("liveExecute",e);
        }
        return "{ \"code\":-1 }";
    }

    @PostMapping("/im/execute")
    @ResponseBody
    public String imExecute(@RequestParam IMCallBackDTO dto, @RequestBody HttpEntity data){

        logger.info("imExecute-request："+data+","+dto);

        if(proxyCallBackHost!=null){
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(proxyCallBackHost).append("/manager/im/execute?");
            stringBuilder.append("ClientIP=").append(dto.getClientIP()).append("&");
            stringBuilder.append("CallbackCommand=").append(dto.getCallbackCommand()).append("&");
            stringBuilder.append("SdkAppid=").append(dto.getSdkAppid()).append("&");
            stringBuilder.append("contenttype=").append(dto.getContenttype()).append("&");
            stringBuilder.append("OptPlatform=").append(dto.getOptPlatform());

            asyncService.requestProxyHost(stringBuilder.toString(), data.getBody().toString());
        }

        return "{" +
                "    \"ActionStatus\": \"OK\"," +
                "    \"ErrorInfo\": \"\"," +
                "    \"ErrorCode\": 0 " +
                "}";
    }

    /**
     * 微信支付回调

     * @return
     */
    @PostMapping("/weixin/pay/execute")
    @ResponseBody
    public WeiXinPayCallBackVO weiXinPayExecute(HttpServletRequest request){
        InputStream is=null;
        String orderNo=null;
        try{
            is=request.getInputStream();
            String xmlStr= IOUtils.toString(is);
            logger.info("weiXinPayExecute-request："+xmlStr);
            Map<String,String> map=Tools.xmlCastMap(xmlStr);
            if(!map.containsKey("out_trade_no"))return new WeiXinPayCallBackVO("FAIL","ok");
            orderNo=map.get("out_trade_no");
            if(map!=null && "SUCCESS".equals(map.get("return_code"))&& "SUCCESS".equals(map.get("result_code")) ){
               if(StringUtils.isBlank(paySignClose) && !SignUtil.wxPaySign(map)){
                    logger.info("微信支付回调验签不成功");

                    CacheUtil.put("pay","order-"+orderNo,"fail");

                    return new WeiXinPayCallBackVO("FAIL","ok");

                }
                Object data =(Object) CacheUtil.get("pay","weixin-pay-"+orderNo);
               logger.info("weiXinPayExecute->weixin-pay-"+orderNo+"->"+data);
                if(data!=null){
                    ResponseVO res=orderService.updateOrderPayStatus(PayStatusEnum.PayStatus1,orderNo);
                    logger.info("weiXinPayExecute->weiXinPayExecute->更新支付状态->"+res);
                    if(res.getReCode()==1){
                        return new WeiXinPayCallBackVO("SUCCESS","ok");

                    }
                }else{
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                }

            }

        }catch (Exception e){
            CacheUtil.put("pay","order-"+orderNo,"fail");
            logger.error("微信支付回调失败",e);
        }finally {
            IOUtils.closeQuietly(is);
        }

        return new WeiXinPayCallBackVO("FAIL","ok");
    }
    /**
     * 微信申请退款回调

     * @return
     */
    @PostMapping("/weixin/refund/execute")
    @ResponseBody
    public WeiXinPayCallBackVO weiXinRefundExecute(HttpServletRequest request){
        InputStream is=null;
        try{
            is=request.getInputStream();
            String xmlStr= IOUtils.toString(is);
            logger.info("weiXinRefundExecute->request："+xmlStr);
            Map<String,String> map=Tools.xmlCastMap(xmlStr);
            if(map!=null && "SUCCESS".equals(map.get("return_code")) ){
               if(StringUtils.isBlank(paySignClose) && !SignUtil.wxPaySign(map)){
                    logger.info("微信申请退款回调验签不成功");

                    CacheUtil.put("pay","refund-"+map.get("out_trade_no"),"fail");

                    return new WeiXinPayCallBackVO("FAIL","ok");

                }
                Object data =(Object) CacheUtil.get("pay","weixin-refund-"+map.get("out_trade_no"));
               logger.info("weiXinRefundExecute->weixin-refund-"+map.get("out_trade_no")+"->"+data);
                if(data!=null){
                    ResponseVO res=orderService.updateSimpleOrderPayStatus(PayStatusEnum.PayStatus2,map.get("out_trade_no"));
                    logger.info("weiXinRefundExecute->更新支付状态->"+res);
                    if(res.getReCode()==1){
                        CacheUtil.put("pay","refund-"+map.get("out_trade_no"),"success");
                        return new WeiXinPayCallBackVO("SUCCESS","ok");

                    }else{
                        CacheUtil.put("pay","refund-"+map.get("out_trade_no"),"fail");

                    }
                }else{
                    CacheUtil.put("pay","refund-"+map.get("out_trade_no"),"fail");
                }

            }

        }catch (Exception e){
            logger.error("微信退款回调失败",e);
        }finally {
            IOUtils.closeQuietly(is);
        }

        return new WeiXinPayCallBackVO("FAIL","ok");
    }

    /**
     * 支付宝支付回调
     * @param dto
     * @return
     */
    //@PostMapping("/alipay/execute")
    @RequestMapping("alipay/execute")
    @ResponseBody
    public String aliPayExecute(@RequestParam Map<String,String> dto){
        String orderNo=null;
        try {
            logger.info("aliPayExecute->request："+dto);
            if(!dto.containsKey("out_trade_no") || StringUtils.isBlank(dto.get("out_trade_no")))return "fail";
            orderNo=dto.get("out_trade_no");
            if(dto!=null && ("TRADE_FINISHED".equals(dto.get("trade_status")) || "TRADE_SUCCESS".equals(dto.get("trade_status")))){
                if(StringUtils.isBlank(paySignClose) && !SignUtil.aliPaySign(dto)){
                    logger.info("支付宝回调验签不成功");
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    return "fail";
                }
                Object data =(Object) CacheUtil.get("pay","alipay-pay-"+orderNo);
                logger.info("weiXinPayExecute->alipay-pay-"+dto.get("out_trade_no")+"->"+data);

                if(data!=null){
                    ResponseVO rs=this.orderService.updateOrderPayStatus(PayStatusEnum.PayStatus1,orderNo);
                    logger.info("weiXinPayExecute->aliPayExecute->更新支付状态->"+rs);

                    if(rs.getReCode()==1){
                        return "success";

                    }

                }else{
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                }

            }
        }catch (Exception e){
            CacheUtil.put("pay","order-"+orderNo,"fail");
            logger.error("支付宝支付回调失败",e);

        }
        return "fail";
    }
}
