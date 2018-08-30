package com.ddw.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.UserInfoPO;
import com.ddw.config.DDWGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.HttpUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class IMApiUtil {
    //private static String baseUri="https://console.tim.qq.com/v4/$servicename/$command?sdkappid=$sdkappid&identifier=$identifier&usersig=$usersig&random=99999999&contenttype=json";
    private static String baseUri="https://console.tim.qq.com/v4";

    private static DDWGlobals ddwGlobals;

    public static void setDdwGlobals(DDWGlobals ddwGlobals) {
        IMApiUtil.ddwGlobals = ddwGlobals;
    }

    /**
     {
     "Owner_Account":"ddwadmin",
     "Type":"ChatRoom",
     "GroupId":"ddw1",
     "Name": "逗逗2",
     "ApplyJoinOption":"FreeAccess",
     "MemberList": [
     {
     "Member_Account": "gen",
     "Role": "Admin"
     },
     {
     "Member_Account": "gg"
     }
     ]
     }
     * @param sign
     * @return
     */
    public static String createGroup(String userId,String groupId,String groupName)throws Exception{
        //{"ActionStatus":"OK","ErrorCode":0,"GroupId":"ddw3"}
        initDDWGlobls();
        Map map=new HashMap();
        map.put("Owner_Account", LiveRadioConstant.ADMIN_ACCOUNT);
        map.put("Type","ChatRoom");
        map.put("GroupId",groupId);
        map.put("Name",groupName);
        map.put("ApplyJoinOption","FreeAccess");
        List MemberList=new ArrayList();
        Map mem=new HashMap();
       mem.put("Member_Account",userId);
        mem.put("Role","Admin");
        MemberList.add(mem);
        map.put("MemberList",MemberList);
       // tls_sigcheck ts=Toolsddw.getWebapplication().getBean(tls_sigcheck.class);

        StringBuilder sb=new StringBuilder();
        sb.append(baseUri);
        sb.append("/group_open_http_svc");
        sb.append("/create_group");

        sb.append(createSignParams(LiveRadioConstant.ADMIN_ACCOUNT));
       return  HttpUtil.sendHtpps(sb.toString(), JSON.toJSONString(map));

    }
    public static Map getMemberNum(List<String> groupIds)throws Exception{
        initDDWGlobls();
        Map param=new HashMap();
        param.put("GroupIdList",groupIds);
        StringBuilder sb=new StringBuilder();
        sb.append(baseUri);
        sb.append("/group_open_http_svc/get_group_info");
        sb.append(createSignParams(LiveRadioConstant.ADMIN_ACCOUNT));
        String callBack=HttpUtil.sendHtpps(sb.toString(), JSON.toJSONString(param));
        if(StringUtils.isBlank(callBack)){
            throw new GenException("获取群成员数异常");
        }
        JSONObject jsonObject= JSON.parseObject(callBack);
        String actionStatus=jsonObject.getString("ActionStatus");
        if(!"OK".equals(actionStatus)){
            throw new GenException("获取群成员数异常->"+callBack);
        }
        Map m=new HashMap();
        JSONArray array=jsonObject.getJSONArray("GroupInfo");
        JSONObject item=null;
        for(int i=0;i<array.size();i++){
            item=array.getJSONObject(i);
            if(item.getInteger("ErrorCode")==0){

                m.put(item.getString("GroupId"),item.getInteger("MemberNum"));
            }
        }
        return m;
    }
    public static boolean destoryGroup(String groupId){
        Map param=new HashMap();
        param.put("GroupId",groupId);
        StringBuilder sb=new StringBuilder();
        sb.append(baseUri);
        sb.append("/group_open_http_svc/destroy_group");
        sb.append(createSignParams(LiveRadioConstant.ADMIN_ACCOUNT));
        return for5Sends(sb,param);
    }

    public static String sendGroupMsg(String groupId, ResponseVO responseVO){
        StringBuilder sb=new StringBuilder();
        sb.append(baseUri);
        sb.append("/group_open_http_svc");
        sb.append("/send_group_msg");

        sb.append(createSignParams(LiveRadioConstant.ADMIN_ACCOUNT));

        Map baseMap=new HashMap();
        baseMap.put("GroupId",groupId);
        baseMap.put("Random",RandomStringUtils.randomNumeric(10));
        List data=new ArrayList();
        Map dataMap=new HashMap();
        dataMap.put("MsgType","TIMCustomElem");
        Map ext=new HashMap();
        ext.put("ext",responseVO);
        dataMap.put("MsgContent",ext);
        data.add(dataMap);
        baseMap.put("MsgBody",data);
        return  HttpUtil.sendHtpps(sb.toString(), JSON.toJSONString(baseMap));


    }
    public static boolean importUser(UserInfoPO userInfoPO,Integer imUserType){
        Map param=new HashMap();
        param.put("Identifier",userInfoPO.getOpenid());
        param.put("Nick", StringUtils.isBlank(userInfoPO.getNickName())?userInfoPO.getUserName():userInfoPO.getNickName());
        param.put("FaceUrl",userInfoPO.getHeadImgUrl());
        param.put("Type",imUserType);

        StringBuilder sb=new StringBuilder();
        sb.append(baseUri);
        sb.append("/im_open_login_svc/account_import");
        sb.append(createSignParams(LiveRadioConstant.ADMIN_ACCOUNT));

       return for5Sends(sb,param);


    }
    private static boolean  for5Sends(StringBuilder sb,Map param){
        String callBack=null;
        for(int i=0;i<5;i++){
            callBack=HttpUtil.sendHtpps(sb.toString(), JSON.toJSONString(param));
            if(StringUtils.isBlank(callBack)){
                continue;
            }else{
                JSONObject jsonObject=JSON.parseObject(callBack);
                Integer errorCode=jsonObject.getInteger("ErrorCode");
                if(errorCode.equals(0) || errorCode==0){
                    return true;
                }
            }
        }
        return false;
    }
    private static void initDDWGlobls(){
        if(ddwGlobals==null){
            ddwGlobals= Tools.getWebapplication().getBean(DDWGlobals.class);
        }
    }

    private static String createSignParams(String identifier){
        initDDWGlobls();
        tls_sigcheck ts=new tls_sigcheck();
        ts.setDdwGlobals(ddwGlobals);
        StringBuilder sb=new StringBuilder();
        sb.append("?");
        String userSign=(String) CacheUtil.get("imSign","sign");
        if(StringUtils.isBlank(userSign)){
            userSign=ts.createSign(identifier);
           // CacheUtil.put("imSign","sign",userSign);
        }
        sb.append("usersig=").append(userSign).append("&");
        sb.append("identifier=").append(LiveRadioConstant.ADMIN_ACCOUNT).append("&");
        sb.append("sdkappid=").append(LiveRadioConstant.SDK_API).append("&");
        sb.append("random=").append(RandomStringUtils.randomNumeric(10)).append("&");
        sb.append("contenttype=json");
        return sb.toString();

    }
    public static void main(String[] args)throws Exception {
        tls_sigcheck ts=new tls_sigcheck();

        Properties pro=new Properties();
        pro.load(tls_sigcheck.class.getClassLoader().getResourceAsStream("application-dev.properties"));
        DDWGlobals ddwGlobals=new DDWGlobals();
        ddwGlobals.setLibpath(pro.getProperty("im.tls.sigcheck.lib.path"));
        ddwGlobals.setPrivateKeypath(pro.getProperty("im.tls.sigcheck.lib.privatekey.path"));
        ts.setDdwGlobals(ddwGlobals);
        setDdwGlobals(ddwGlobals);

       /* UserInfoPO userInfoPO=new UserInfoPO();
        userInfoPO.setOpenid("gen");
        userInfoPO.setNickName("我是测试的");
        userInfoPO.setHeadImgUrl("http://wx.qlogo.cn/mmopen/Q3auHgzwzM70nZPOZLa6PTYzFKZp4xm9KRQITutLibgqjUAesTBciaFCpSzUicPwHT7mKeYDHhGYJX1FJlAPphe3UWKKvOOYC8dGNbSuibz9MOI/132");
        System.out.println(importUser(userInfoPO,null));*/

       System.out.println(getMemberNum(Arrays.asList("1_8_180503191514","1_8_180504013649","1_59_180829224224","1_41_180829211729")));;
    }
}
