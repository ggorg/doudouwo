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
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            ResponseVO responseVO=this.liveRadioService.handleLiveRadioStatus(dto.getStream_id(),dto.getEvent_type());
            if(responseVO.getReCode()==1){
                if(dto.getEvent_type().equals(LiveEventTypeEnum.eventType0.getCode())){
                    Map<String,Integer> map=(Map<String,Integer>)cacheService.get("backRoom");
                    if(map.containsKey(dto.getStream_id())){
                        map.remove(dto.getStream_id());
                        cacheService.set("backRoom",map);
                    }
                }
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
     * @param dto
     * @return
     */
    @PostMapping("/weixin/pay/execute")
    @ResponseBody
    public WeiXinPayCallBackVO weiXinPayExecute(@RequestBody WeiXinPayCallBackDTO dto){
        try{
            logger.info("weiXinPayExecute-request："+dto);
            if(dto!=null && "SUCCESS".equals(dto.getReturn_code())&& "SUCCESS".equals(dto.getResult_code()) ){
                if(!SignUtil.wxPaySign(dto)){
                    logger.info("微信支付回调验签不成功"+dto);

                    CacheUtil.put("pay","order-"+dto.getOut_trade_no(),"fail");

                    return new WeiXinPayCallBackVO("FAIL","ok");

                }
                Object data =(Object) CacheUtil.get("pay","weixin-pay-"+dto.getOut_trade_no());
                if(data!=null){
                    ResponseVO res=orderService.updateOrderPayStatus(PayStatusEnum.PayStatus1,dto.getOut_trade_no());
                    if(res.getReCode()==1){
                        return new WeiXinPayCallBackVO("SUCCESS","ok");

                    }

                }else{
                    CacheUtil.put("pay","order-"+dto.getOut_trade_no(),"fail");
                }

            }

        }catch (Exception e){
            logger.error("微信支付回调失败");
        }

        return new WeiXinPayCallBackVO("FAIL","ok");
    }

    /**
     * 支付宝支付回调
     * @param dto
     * @return
     */
    @PostMapping("/alipay/execute")
    @ResponseBody
    public String aliPayExecute(AliPayCallBackDTO dto){
        try {
            logger.info("aliPayExecute->request："+dto);
            if(dto!=null && ("TRADE_FINISHED".equals(dto.getTrade_status()) || "TRADE_SUCCESS".equals(dto.getTrade_status()))){
                Object data =(Object) CacheUtil.get("pay","alipay-pay-"+dto.getOut_trade_no());
                if(data!=null){
                    ResponseVO rs=this.orderService.updateOrderPayStatus(PayStatusEnum.PayStatus1,dto.getOut_trade_no());
                    if(rs.getReCode()==1){
                        return "success";

                    }

                }else{
                    CacheUtil.put("pay","order-"+dto.getOut_trade_no(),"fail");
                }

            }
        }catch (Exception e){
            logger.error("支付宝支付回调失败");

        }
        return "fail";
    }
}
