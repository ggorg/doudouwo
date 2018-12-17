package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.LoginPublicDTO;
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



    public static ResponseApiVO oauth(LoginPublicDTO dto){
        String str=null;
        if(StringUtils.isNotBlank(dto.getAccessToken())){
            String openId=dto.getOpenId();
            Map data=new HashMap();
            data.put("access_token",dto.getAccessToken());
            data.put("openid",openId);
            if(dto.getRegisterType()==1){

                str=HttpUtil.doGet("https://api.weixin.qq.com/sns/auth",data);
                if(StringUtils.isNotBlank(str) && JSONObject.parseObject(str).getInteger("errcode")==0){
                    str=HttpUtil.doGet("https://api.weixin.qq.com/sns/userinfo",data);
                    if(StringUtils.isNotBlank(str)){
                        JSONObject json=JSONObject.parseObject(str);
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
                            logger.info("微信登录获取信息封装的userInfoDTO:"+BeanToMapUtil.beanToMap(userInfoDTO));
                            return new ResponseApiVO(1,"成功",userInfoDTO);
                        }

                    }
                }

            }else if(dto.getRegisterType()==2){
                data.put("oauth_consumer_key", ApiConstant.QQ_APP_ID);
                str=HttpUtil.doGet("https://graph.qq.com/user/get_user_info",data);
                if(StringUtils.isNotBlank(str) && JSONObject.parseObject(str).getInteger("ret")==0){
                    JSONObject json=JSONObject.parseObject(str);
                    UserInfoDTO userInfoDTO=new UserInfoDTO();
                    userInfoDTO.setOpenid(openId);
                    userInfoDTO.setRealOpenid(openId);
                    userInfoDTO.setUnionID(openId);
                    userInfoDTO.setNickName(json.getString("nickname"));
                    userInfoDTO.setUserName(json.getString("nickname"));
                    userInfoDTO.setSex(json.getString("gender").equals("男")?1:2);
                    userInfoDTO.setHeadImgUrl(json.getString("figureurl_qq_1"));
                    userInfoDTO.setRegisterType(2);
                    logger.info("QQ登录获取信息封装的userInfoDTO:"+BeanToMapUtil.beanToMap(userInfoDTO));
                    return new ResponseApiVO(1,"成功",userInfoDTO);
                }
            }

        }
        logger.error("微信登录失败-》request："+dto+",response："+str);
        return new ResponseApiVO(-2,"登录失败",null);
    }

    private static String QQ_GET_USER_INFO_URL="http://openapi.tencentyun.com/v3/user/get_info";
    public static ResponseApiVO qqOauth(LoginQQDTO dto)throws Exception{
        Map dtoMap= BeanToMapUtil.beanToMap(dto,true);
        dtoMap.put("appid", ApiConstant.QQ_APP_ID);
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
       return HMAC_SHA1.genHMAC(builder.toString(), ApiConstant.QQ_APP_KEY+"&");
    }
    public static void main(String[] args)throws Exception{



    }
}
