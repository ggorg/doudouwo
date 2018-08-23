package com.ddw.util;

import com.gen.common.util.CacheUtil;
import com.github.qcloudsms.SmsSingleSender;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * 腾讯云短信
 */
public class MsgUtil {
    private static final Logger logger = Logger.getLogger(MsgUtil.class);

    private final static String sign_id="162689";

    //门票ID
    private final static Integer ticket_id=176003;
    //竞价ID
    private final static Integer bid_id=176000;
    //实名验证ID
    private final static Integer cert_id=175991;

    /**
     * 您申请的实名认证短信验证码{1}
     * @param telphone
     * @return
     * @throws Exception
     */
    public static String sendVaildCode(String telphone)throws Exception{
        String random= RandomStringUtils.randomNumeric(6);
        commonModel(cert_id,telphone,random);
        logger.info("实名验证验证码："+random);
        CacheUtil.put("validCodeCache","telphone-"+telphone,random);
        return random;
    }

    /**
     * 校验验证码
     * @param telphone
     * @param validCode
     * @return
     */
    public static boolean verifyCode(String telphone,String validCode){
        String string=(String)CacheUtil.get("validCodeCache","telphone-"+telphone);
        if(StringUtils.isBlank(string)){
            return false;
        }
        if(string.equals(validCode)){
            return true;
        }
        return false;
    }

    /**
     * 	你预定的门票{1}，请您在该时间段来店咨询消费。
     * @param telphone
     * @param content
     * @throws Exception
     */
    public static void  sendTicketMsg(String telphone,String content)throws Exception{
        commonModel(ticket_id,telphone,content);
    }

    /**
     * 尊敬的用户您好，您预约的逗逗窝直播已经选择了您，请{1}于半小时到店开始体验
     * @param telphone
     * @param content
     * @throws Exception
     */
    public static void  sendBidMsg(String telphone,String content)throws Exception{
        commonModel(bid_id,telphone,content);
    }
    public static void commonModel(Integer templateId,String telphone,String content)throws Exception{
        SmsSingleSender sender=new SmsSingleSender(MsgConstant.APP_ID,MsgConstant.APP_KEY);
        ArrayList list=new ArrayList();
        list.add(content);
        sender.sendWithParam("86",telphone,templateId,list,"","","");
    }

    public static void main(String[] args) throws Exception{
        sendVaildCode("13416605209");
        System.out.println();
    }
}
