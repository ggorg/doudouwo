package com.ddw.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.services.LiveRadioService;
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
            System.out.println(dto);
            if(proxyCallBackHost!=null){
                return HttpUtil.doPost(proxyCallBackHost+"/manager/live/execute", JSON.toJSONString(dto));
            }else{
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
            }

        }catch (Exception e){
            logger.error("liveExecute",e);
        }
        return "{ \"code\":-1 }";
    }

    @PostMapping("/im/execute")
    @ResponseBody
    public String imExecute(@RequestParam IMCallBackDTO dto, @RequestBody HttpEntity data){
        System.out.println(dto);
        System.out.println(data);
        if(proxyCallBackHost!=null){
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(proxyCallBackHost).append("/manager/im/execute?");
            stringBuilder.append("ClientIP=").append(dto.getClientIP()).append("&");
            stringBuilder.append("CallbackCommand=").append(dto.getCallbackCommand()).append("&");
            stringBuilder.append("SdkAppid=").append(dto.getSdkAppid()).append("&");
            stringBuilder.append("contenttype=").append(dto.getContenttype()).append("&");
            stringBuilder.append("OptPlatform=").append(dto.getOptPlatform());
            return HttpUtil.doPost(stringBuilder.toString(), JSON.toJSONString(dto));
        }else{

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
        if(dto!=null && "SUCCESS".equals(dto.getReturn_code())&& "SUCCESS".equals(dto.getResult_code()) ){

            TreeMap treeMap =(TreeMap) CacheUtil.get("pay","weixin-pay-"+dto.getOut_trade_no());
            if(treeMap==null){
                return new WeiXinPayCallBackVO("FAIL","ok");
            }
            return new WeiXinPayCallBackVO("SUCCESS","ok");
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
    public String aliPayExecute(@RequestParam AliPayCallBackDTO dto){
        if(dto!=null && ("TRADE_FINISHED".equals(dto.getTrade_status()) || "TRADE_SUCCESS".equals(dto.getTrade_status()))){
            return "success";
        }
        return "fail";
    }
}
