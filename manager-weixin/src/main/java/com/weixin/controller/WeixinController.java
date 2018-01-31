package com.weixin.controller;


import com.weixin.services.CoreService;
import com.weixin.util.SignUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 核心请求处理类
 */
@Controller
@RequestMapping(value = "weixin")
public class WeixinController {

    private static final Logger logger = Logger.getLogger("WeixinController");

    @Autowired
    private CoreService coreService;

    /**
     * 确认请求来自微信服务器
     */
    @ResponseBody
    @RequestMapping(value = "receiveWX", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String doGet(HttpServletRequest request) {
        String ret = null;
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            ret = echostr;
        }
        return ret;
    }

    /**
     * 处理微信服务器发来的消息
     */
    @ResponseBody
    @RequestMapping(value = "receiveWX", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public String doPost(HttpServletRequest request) {
        // 调用核心业务类接收消息、处理消息
        String respMessage = coreService.processRequest(request);
        // 响应消息
        return respMessage;
    }

}
