package com.weixin.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
    public static String base32Encode(String input){
        Base32 base32 = new Base32();
        return base32.encodeAsString(input.getBytes()).replace("=", "");
    }
    public static String base32Decode(String input){
        Base32 base32 = new Base32();
        return  new String( base32.decode(input));
    }

    public static String createOauthUrl(String appid,String oauthMainUrl,String page,Object ...extendParam){
        try {
            JSONObject state = new JSONObject();
            state.put("appid",appid);
            state.put("page",page);
            if(extendParam!=null && extendParam.length>0){
                StringBuilder builder=new StringBuilder();
                for(Object obj:extendParam){
                    builder.append(obj.toString()).append("_");
                }
                state.put("param",builder.deleteCharAt(builder.length()-1));
            }
            StringBuffer oauthUrl = new StringBuffer();
            oauthUrl = oauthUrl.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(appid);
            oauthUrl = oauthUrl.append("&redirect_uri=").append(URLEncoder.encode(oauthMainUrl,"utf-8"));
            oauthUrl = oauthUrl.append("&response_type=code&scope=snsapi_userinfo&state=");
            oauthUrl = oauthUrl.append(CommonUtil.base32Encode(state.toString())).append("#wechat_redirect");
            return oauthUrl.toString();
        }catch (Exception e){
            logger.error("CommonUtil->createOauthUrl->系统异常",e);
            return null;
        }

    }

    public static void main(String[] args) {
        Object[] extendParam={1,2,3,4};
        StringBuilder builder=new StringBuilder();
        for(Object obj:extendParam){
            builder.append(obj.toString()).append("_");
        }
        builder.deleteCharAt(builder.length()-1);
        JSONObject json=new JSONObject();
        json.put("param",builder);
        System.out.println(json.getString("param"));
    }
}
