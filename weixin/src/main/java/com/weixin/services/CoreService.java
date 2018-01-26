package com.weixin.services;


import com.weixin.dao.UserInfoMapper;
import com.weixin.entity.*;
import com.weixin.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.weixin.core.beans.message.req.TextMessage;
import com.weixin.dao.PubweixinMapper;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 核心服务类
 */
@Service
public class CoreService {

    private final Logger logger = LoggerFactory.getLogger(CoreService.class);
            
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private PubweixinMapper pubweixinMapper;
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private MsgService msgService;
    @Autowired
    private EventKeyService eventKeyService;
    @Autowired
    private EventRuleService eventRuleService;
    @Autowired
    private MessageService messageService;

    /**
     * 处理微信发来的请求
     * 
     * @param request
     * @return
     */
    public String processRequest(HttpServletRequest request) {
        String respMessage = null;
        // 第三方用户唯一凭证
        String appid = "";
        try {
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            logger.info("CoreService->processRequest->微信回调数据->{}",requestMap);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号原始ID
            String toUserName = requestMap.get("ToUserName");
            Pubweixin pubweixin = pubweixinMapper.selectByOpenid(toUserName);
            appid = pubweixin.getAppid();
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 接收到的消息
            String Content = requestMap.get("Content");

            Msg msg = new Msg();
            msg.setAppid(appid);
            msg.setOpenid(fromUserName);
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                msg.setContent(Content);
                msg.setMsgtype("1");
                //入库消息管理
                msgService.save(msg);
                // 关键词回复处理
                respMessage = autoReplys(Content, fromUserName, pubweixin);
                // 图片消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                //图片标识
                msg.setMsgtype("2");
                //图片链接
                String imageurl = requestMap.get("PicUrl");
                msg.setContent(imageurl);
                msg.setFilepath(imageurl);
                //入库消息管理
                msgService.save(msg);
             // 地理位置消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
//                respContent = "您发送的是地理位置消息！";
             // 链接消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
//                respContent = "您发送的是链接消息！";
             // 音频消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
//                respContent = "您发送的是音频消息！";
            	msg.setMsgtype("5");
            	msg.setContent("[音频]");
                //入库消息管理
                msgService.save(msg);
             // 视频消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
            	msg.setMsgtype("4");
            	msg.setContent("[视频]");
                //入库消息管理
                msgService.save(msg);
             // 小视频消息
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
            	msg.setMsgtype("5");
            	msg.setContent("[小视频]");
                //入库消息管理
                msgService.save(msg);
                // 事件推送
            }else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                String eventKey = requestMap.get("EventKey");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //这里获取用户信息
                    UserInfo ui = userInfoMapper.selectByopenid(fromUserName);
                    if (ui != null) {
                        //曾经关注过的用户
                        UserInfo userInfo = new UserInfo();
                        userInfo.setOpenid(fromUserName);
                        userInfo.setSubscribe("1");
                        userInfo.setSubscribe_time(new Date());
                        userInfoMapper.update(userInfo);
                    } else {
                        UserInfo userInfo = weixinInterfaceService.getUserInfo(appid, fromUserName);
                        // 保存微信用户资料入库
                        weixinUserService.save(userInfo);
                    }

                    //扫描场景码关注时处理
                    if (eventKey.startsWith("qrscene_")) {
                        // 取出场景ID
                        String scene_id = eventKey.split("qrscene_")[1];
                    }
                    EventRule eventRule = eventRuleService.getSubscribe(appid);
                    respMessage = eventRuleReplys(eventRule,fromUserName,pubweixin);
                 // 取消订阅
                }else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    //取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                    UserInfo userInfo = new UserInfo();
                    userInfo.setOpenid(fromUserName);
                    userInfo.setSubscribe("0");
                    userInfoMapper.update(userInfo);
                 // 自定义菜单点击事件
                }else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    logger.info("CoreService->processRequest->微信回调数据->菜单点击事件->eventKey:{},fromUserName:{},pubweixin:{}",eventKey,fromUserName,pubweixin);
                    respMessage = autoReplys(eventKey,fromUserName, pubweixin);
                    logger.info("CoreService->processRequest->微信回调数据->菜单点击事件->返回->respMessage:{}",respMessage);

                    // 二维码扫描事件
                }else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                    // 取出场景ID
                    String scene_id = requestMap.get("EventKey");
                }
            }
            //无任何返回数据时,判断是否有自动回复,有则返回自动回复规则
            if (respMessage == null || respMessage.equals("")) {
//                respContent = "亲，暂不能识别您发送的指令(´･ω･｀
                EventRule eventRule = eventRuleService.getAutoreply(appid);
                respMessage = eventRuleReplys(eventRule,fromUserName,pubweixin);
            }
        } catch (Exception e) {
            logger.error("CoreService->processRequest->处理微信请求->系统异常",e);
        }
        return respMessage;
    }

    /**
     * 回复规则自动回复以及关注后回复触发回复封装
     * @param eventRule 回复规则
     * @param fromUserName 用户openid
     * @param pubweixin
     * @return
     */
    public String eventRuleReplys(EventRule eventRule, String fromUserName, Pubweixin pubweixin){
        String respMessage = null;
        if(eventRule != null){
            // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
            if(eventRule.getContent() != null && !eventRule.getContent().equals("")){
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(pubweixin.getOpenid());
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setContent(eventRule.getContent());
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }else{
                respMessage =messageService.sendArticle(fromUserName,pubweixin.getOpenid(),eventRule.getMessage_id());
            }
        }
        return respMessage;
    }

    /**
     * 关键词自动回复
     * @param content
     * @param fromUserName 用户openid
     * @param pubweixin 公众号信息
     * @return
     */
    public String autoReplys(String content, String fromUserName, Pubweixin pubweixin) {
        String toUserName = pubweixin.getOpenid();
        String respMessage = null;
        List<EventKey> eventKeys = eventKeyService.findList(pubweixin.getAppid());
        for(EventKey ek:eventKeys){
            // 轮询处理关键字回复
            int match = ek.getMatch();
            //匹配规则，1 完全匹配，0 模糊匹配
            if((match == 1 && content.equals(ek.getKey())) || (match == 0 && (content.indexOf(ek.getKey())!= -1 || content.equalsIgnoreCase(ek.getKey())))){
                //根据回复规则id查询返回回复规则实体bean
                EventRule er = eventRuleService.selectById(ek.getRule_id());
                // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
                if(er.getContent() != null && !er.getContent().equals("")){
                    // 回复文本消息
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(new Date().getTime());
                    textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setContent(er.getContent());
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }else{
                    respMessage =messageService.sendArticle(fromUserName,toUserName,er.getMessage_id());
                }
            }
        }
        return respMessage;
    }
}