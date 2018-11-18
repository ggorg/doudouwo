package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.LoginQQDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.UserInfoDTO;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LoginAuthApiUtil {
    private static final Logger logger = Logger.getLogger(LoginAuthApiUtil.class);

    public static ResponseApiVO weiXinOauth(String code){
        Map data=new HashMap<>();
        data.put("appid",PayApiConstant.WEI_XIN_APP_APP_ID);
        data.put("secret",PayApiConstant.WEI_XIN_APP_SECRET);
        data.put("code",code);
        data.put("grant_type","authorization_code");
        String str=HttpUtil.doGet("https://api.weixin.qq.com/sns/oauth2/access_token",data);
        if(StringUtils.isNotBlank(str)){
            JSONObject json=JSONObject.parseObject(str);
            String access_token=null;
            if(json.containsKey("access_token") && StringUtils.isNotBlank((access_token=json.getString("access_token")))){
                String openId=json.getString("openid");
                //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                data.clear();
                data.put("access_token",access_token);
                data.put("openid",openId);
                str=HttpUtil.doGet("https://api.weixin.qq.com/sns/userinfo",data);
                if(StringUtils.isNotBlank(str)){
                    json=JSONObject.parseObject(str);
                    if(json.containsKey("unionid") && StringUtils.isNotBlank(json.getString("unionid"))){

                        UserInfoDTO userInfoDTO=new UserInfoDTO();
                        userInfoDTO.setOpenid(json.getString("unionid"));
                        userInfoDTO.setRealOpenid(json.getString("openid"));
                        userInfoDTO.setUnionID(json.getString("unionid"));
                        userInfoDTO.setNickName(json.getString("nickname"));
                        userInfoDTO.setUserName(json.getString("nickname"));
                        userInfoDTO.setSex(json.getInteger("sex"));
                        userInfoDTO.setHeadImgUrl(json.getString("headimgurl"));
                        userInfoDTO.setPhone(json.containsKey("phone")?json.getString("phone"):"");
                        userInfoDTO.setRegisterType(1);
                        return new ResponseApiVO(1,"成功",userInfoDTO);
                    }

                }
            }
        }
        logger.error("微信登录失败-》"+str);
        return new ResponseApiVO(-2,"登录失败",null);
    }

    private static String QQ_GET_USER_INFO_URL="http://openapi.tencentyun.com/v3/user/get_info";
    public static ResponseApiVO qqOauth(LoginQQDTO dto)throws Exception{
        Map dtoMap= BeanToMapUtil.beanToMap(dto,true);
        dtoMap.put("appid",PayApiConstant.QQ_APP_ID);
        dtoMap.put("pf","qplus");
        dtoMap.put("sig",qqSign(dtoMap));
        String str=HttpUtil.doPost(QQ_GET_USER_INFO_URL,dto);
        if(StringUtils.isNotBlank(str)){
            JSONObject json=JSONObject.parseObject(str);
            if(json.containsKey("ret") && json.getInteger("ret")==0){
                UserInfoDTO userInfoDTO=new UserInfoDTO();
                userInfoDTO.setOpenid(json.getString("openid"));
                userInfoDTO.setRealOpenid(json.getString("openid"));
                userInfoDTO.setUnionID(json.getString("openid"));
                userInfoDTO.setNickName(json.getString("nickname"));
                userInfoDTO.setUserName(json.getString("nickname"));
                userInfoDTO.setSex(json.getString("gender").equals("男")?1:2);
                userInfoDTO.setHeadImgUrl(json.getString("figureurl"));
                userInfoDTO.setRegisterType(2);
                //userInfoDTO.setPhone(json.containsKey("phone")?json.getString("phone"):"");
                return new ResponseApiVO(1,"成功",userInfoDTO);
            }
        }
        logger.error("QQ登录失败-》"+str);
        return new ResponseApiVO(-2,"登录失败",null);
    }
    public static String qqSign(Map data)throws Exception{
        TreeMap treeMap=new TreeMap(data);

        StringBuilder builder=new StringBuilder();
        builder.append("POST").append("&").append(URLEncoder.encode(QQ_GET_USER_INFO_URL,"UTF-8")).append("&");
        Set<String> keys=treeMap.keySet();
        StringBuilder signBuild=new StringBuilder();
        for(String key:keys){
            signBuild.append(key).append("=").append(treeMap.get(key)).append("&");
        }
        builder.append(URLEncoder.encode(signBuild.deleteCharAt(signBuild.length()-1).toString(),"UTF-8"));
       return HMAC_SHA1.genHMAC(builder.toString(),PayApiConstant.QQ_APP_KEY+"&");
    }
    public static void main(String[] args)throws Exception{



    }
}
