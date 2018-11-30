package com.ddw.util;

import com.gen.common.util.CacheUtil;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
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
    //找回支付密码
    private final static Integer paypwd_id=189074;
    //通用验证码模版
    private final static Integer commonVaildCode_id=238997;

    private final static boolean isMsg=true;

    private static String sendMsgTimes(String telphone){
        Integer n=(Integer) CacheUtil.get("msgTimesTimeOut","telphone-"+telphone);
        if(n!=null && n>=5){
            return "-2";
        }
        if(n==null || n==0){
            n=1;
        }else{
            n=n+1;
        }
        CacheUtil.put("msgTimesTimeOut","telphone-"+telphone,n);
        return "1";
    }
    /**
     * 您申请的实名认证短信验证码{1}
     * @param telphone
     * @return
     * @throws Exception
     */
    public static String sendVaildCode(String telphone)throws Exception{
        String random= commonSendVaildCode(telphone,cert_id);
        logger.info(telphone+"实名认证验证码："+random);
        return random;
    }

    /**
     * 您在逗逗窝请求的验证码是：{1}
     * @param telphone
     * @return
     * @throws Exception
     */
    public static String sendOtherVaildCode(String telphone)throws Exception{
        String random= commonSendVaildCode(telphone,commonVaildCode_id);
        logger.info(telphone+"请求验证码："+random);
        return random;
    }
    public static String commonSendVaildCode(String telphone,Integer templateId)throws Exception{
        String string=(String)CacheUtil.get("validCodeCache","telphone-"+telphone);
        if(StringUtils.isNotBlank(string)){
            return "-1";
        }

        if(!isMsg){
            CacheUtil.put("validCodeCache","telphone-"+telphone,"123456");
            return "123456";
        }
        string=sendMsgTimes(telphone);
        if(string.equals("-2")){
            return string;
        }
        String random= RandomStringUtils.randomNumeric(6);
        commonModel(templateId,telphone,random);
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
        logger.info("验证验证码->手机："+telphone+"，输入的验证码："+validCode+"，缓存验证码："+string);
        if(StringUtils.isBlank(string)){
            return false;
        }
        return string.equals(validCode);
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

    /**
     *
     * @param telphone
     * @throws Exception
     */
    public static String  sendPayPwdMsg(String telphone)throws Exception{
        String random= commonSendVaildCode(telphone,paypwd_id);

        logger.info(telphone+"忘记密码："+random);
        return random;
    }

    public static void commonModel(Integer templateId,String telphone,String content)throws Exception{
        SmsSingleSender sender=new SmsSingleSender(MsgConstant.APP_ID,MsgConstant.APP_KEY);
        ArrayList list=new ArrayList();
        list.add(content);
        SmsSingleSenderResult result=sender.sendWithParam("86",telphone,templateId,list,"","","");
        logger.info(result+","+result.errMsg);
    }

    public static void main(String[] args) throws Exception{
        //sendVaildCode("13416605209");
        commonModel(paypwd_id,"13416605209","123456");
        System.out.println();

    }
}
