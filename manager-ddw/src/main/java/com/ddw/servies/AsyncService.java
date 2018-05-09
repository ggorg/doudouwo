package com.ddw.servies;

import com.alibaba.fastjson.JSON;
import com.ddw.controller.CallBackController;
import com.gen.common.util.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncService {
    private final Logger logger = Logger.getLogger(AsyncService.class);

    @Async
    public void requestProxyHost(String url,String jsonStr){
        try {
            HttpUtil.doPost(url, jsonStr);

        }catch (Exception e){
            logger.error("回调测试地址没法访问："+url);
        }

    }
}
