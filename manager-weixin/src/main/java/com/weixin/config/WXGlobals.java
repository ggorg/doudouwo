package com.weixin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WXGlobals {


    @Value("${wx.msg.default.picUrl:http://gen.51ftt.com/wap/images/rp-bg.png}")
    private String defaultPicUrl;

    @Value("${wx.test.openid:}")
    private String testOpenid;

    @Value("${wx.oauth.redirectUri:http://gen.51ftt.com/weixin/oauth}")
    private String redirectUri;

    @Value("${wx.oauth.jump.url}")
    private String oauthJumUrl;

    public String getDefaultPicUrl() {
        return defaultPicUrl;
    }

    public void setDefaultPicUrl(String defaultPicUrl) {
        this.defaultPicUrl = defaultPicUrl;
    }

    public String getTestOpenid() {
        return testOpenid;
    }

    public void setTestOpenid(String testOpenid) {
        this.testOpenid = testOpenid;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getOauthJumUrl() {
        return oauthJumUrl;
    }

    public void setOauthJumUrl(String oauthJumUrl) {
        this.oauthJumUrl = oauthJumUrl;
    }

    private Map<String,String> mapOauthJumUrl;

    public String getOauthJumUrlByKey(String key){
        if(mapOauthJumUrl==null){
            mapOauthJumUrl=new HashMap();
            String[] jumpurls=oauthJumUrl.split(",");
            for(String j:jumpurls){
                int index=j.indexOf(":");
                mapOauthJumUrl.put(j.substring(0,index),j.substring(index+1,j.length()));
            }
        }
        return mapOauthJumUrl.get(key);
    }

}
