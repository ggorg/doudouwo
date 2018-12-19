package com.weixin.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.util.BaseTokenUtil;
import com.gen.common.services.CacheService;
import com.gen.common.util.CommonUtil;
import com.gen.common.util.TydicDES;
import com.gen.common.vo.ResponseVO;
import com.weixin.config.WXGlobals;
import com.weixin.core.pojo.AccessToken;
import com.weixin.entity.Pubweixin;
import com.weixin.entity.UserInfo;
import com.weixin.entity.UserInfoDTO;
import com.weixin.services.*;
import com.weixin.util.WeiXinTools;
import com.weixin.util.WeixinUtil;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网页授权获取用户基本信息
 *
 * @author Jacky
 */
@Controller
@RequestMapping(value = "weixin")
public class WeixinOauthController {
    private static final Logger logger = LoggerFactory.getLogger(WeixinOauthController.class);

    @Autowired
    private WeixinUtil weixinUtil;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private WXGlobals WXGlobals;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private OldBringingNewService oldBringingNewService;
    @Autowired
    private UserInfoService userInfoService;



    /**
     * 通过code换取网页授权access_token
     */
    private static final String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    /** 刷新access_token */
    //private static final String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    
    /**
     * 获取用户资料
     */
    private static final String USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    @RequestMapping(value = "oauth")
    public String oauth(String code, String state, HttpServletRequest request, RedirectAttributes attributes,HttpSession session,Model model) {
        // 判断用户是否授权
        logger.info("WeixinOauthController->oauth->code:{},{}",code,state);
        if (code != null && code.length() > 0) {
            // 解析经过Base32编码后传递过来的json参数,state长度最长为128
            //{"appid":"wx695033b74d733b6f","page":"redpack"}  Base32编码 PMRGC4DQNFSCEORCO54DMOJVGAZTGYRXGRSDOMZTMI3GMIRMEJYGCZ3FEI5CE4TFMRYGCY3LEJ6Q
            JSONObject json = JSONObject.parseObject(new String(new Base32().decode(state)));
            String appid = json.getString("appid");
            // 根据传递过来的appid匹配数据库中公众号
            //Pubweixin pubweixin = pubWeixinService.selectByAppid(json.get("appid").toString());
            Pubweixin pubweixin = pubWeixinService.selectByAppid(appid);
            logger.info("pubweixin:"+pubweixin);
            String openid=null;
            UserInfo ui = new UserInfo();
            if (pubweixin != null) {
                String requestUrl = ACCESS_TOKEN.replace("APPID", pubweixin.getAppid()).replace("SECRET", pubweixin.getAppsecret()).replace("CODE", code);

                // 通过code换取网页授权access_token
                logger.info("WeixinOauthController->oauth->请求地址->{}",requestUrl);

                JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
                logger.info("WeixinOauthController->oauth->响应->{}",jsonObject);
                // 如果授权则继续，没有授权则直接返回
                if (jsonObject != null && jsonObject.containsKey("openid")) {
                    /** 关注状态，0未关注，1已关注，-1数据库没数据 */
                    int subscribe = -1;
                    /*用户openid */
                    openid = jsonObject.get("openid").toString();
                    ui = weixinUserService.selectByopenid(openid);
                    if (ui == null) {
                        subscribe = -1;
                    } else {
                        subscribe = ui.getSubscribe().equals("关注") ? 1 : 0;
                    }

                    // 拥有获取用户信息权限
                    if (subscribe == -1 && jsonObject.get("scope").toString().contains("snsapi_userinfo")) {
                        // TODO 校验access_token是否有效，无效调用刷新获取access_token（非必须）
                        JSONObject userinfo = this.userinfo(jsonObject.get("access_token").toString(), openid);
                        ui = weixinUtil.parseJsonToUserInfo(userinfo, openid, pubweixin.getAppid());
                        ui.setSubscribe("0");
                        // 用户信息入库
                        weixinUserService.save(ui);
                    }
                }
            }
            //TODO 这里根据state传递过来的参数page,跳转到对应页面
            if(json.containsKey("page")){
                String page = json.getString("page");
                String urlAppendParam=null;
                if("redpack".equals(page)){
                    return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "pages/manager/weixin/menu";
                }
                if("obn".equals(page)){
                    // 这里绑定老带新,然后跳转到下载APP页面
                    if(json.containsKey("param")){
                        Map oldUser = oldBringingNewService.getOpenid(json.get("param").toString());
                        if(oldUser != null){
                            try {
                                if (userInfoService.countUser(ui.getUnionid())==0){
                                    String oldOpenid = oldUser.get("openid").toString();
                                    String headImgUrl = oldUser.get("headImgUrl").toString();
                                    String nickName = oldUser.get("nickName").toString();
                                    logger.info("绑定老带新oldOpenid:["+oldOpenid+"]openid:["+openid+"]");
                                    oldBringingNewService.save(oldOpenid,openid,ui.getNickname(),ui.getHeadimgurl());
                                    //注册账号
                                    UserInfoDTO userInfoDTO = new UserInfoDTO();
                                    userInfoDTO.setHeadImgUrl(ui.getHeadimgurl());
                                    userInfoDTO.setNickName(ui.getNickname());
                                    userInfoDTO.setOpenid(ui.getUnionid());
                                    userInfoDTO.setRealOpenid(openid);
                                    userInfoDTO.setUnionID(ui.getUnionid());
                                    userInfoDTO.setUserName(ui.getNickname());
                                    userInfoDTO.setSex(Integer.valueOf(ui.getSex()));
                                    userInfoDTO.setRegisterType(1);
                                    userInfoService.save(userInfoDTO);

                                    Map map=new HashMap();
                                    map.put("headImgUrl",headImgUrl);
                                    map.put("nickName",nickName);

                                    urlAppendParam=Base64Utils.encodeToString(JSONObject.toJSONString(map).getBytes());
                                }
                            } catch (Exception e) {
                                logger.error("WeixinOauthController->oauth->老带新注册新用户",e);
                            }
                        }
                    }
                }else if("shop".equals(page)){
                    logger.info("h5商城{}",page);
                    try {
                        Map userMap=userInfoService.getUser(ui.getUnionid());
                        Integer userId=null;
                        if (userMap==null || userMap.isEmpty() || userMap.size()==0){
                            //注册账号
                            UserInfoDTO userInfoDTO = new UserInfoDTO();
                            userInfoDTO.setHeadImgUrl(ui.getHeadimgurl());
                            userInfoDTO.setNickName(ui.getNickname());
                            userInfoDTO.setOpenid(ui.getUnionid());
                            userInfoDTO.setRealOpenid(openid);
                            userInfoDTO.setUnionID(ui.getUnionid());
                            userInfoDTO.setUserName(ui.getNickname());
                            userInfoDTO.setSex(Integer.valueOf(ui.getSex()));
                            userInfoDTO.setRegisterType(1);
                           ResponseVO<Integer> res= userInfoService.save(userInfoDTO);
                            userId=res.getData();
                        }else{
                            userId=(Integer) userMap.get("id");
                        }

                        String[] params=json.getString("param").split("_");
                        Map cookieM=new HashMap();
                        String base64Token=BaseTokenUtil.createToken(ui.getUnionid());
                        cookieM.put("tableNumber",params[1]);
                        cookieM.put("t",base64Token);
                        BaseTokenUtil.putUserIdAndStoreId(base64Token,userId,Integer.parseInt(params[0]),openid);

                        urlAppendParam= URLEncoder.encode(TydicDES.encodeValue(JSONObject.toJSONString(cookieM)),"utf-8");
                    }catch (Exception e){
                        logger.error("WeixinOauthController->oauth->h5商城跳转失败",e);

                    }
                }
                String jumpUrlValue= WXGlobals.getOauthJumUrlByKey(page);
                if(StringUtils.isNotBlank(jumpUrlValue)){

                    AccessToken at=weixinInterfaceService.getTokenByAppid(appid);
                    logger.info("WeixinOauthController->oauth->配置文件url->url:{}",jumpUrlValue);
                    WeiXinTools.initTicket(at.getTicket(),at.getAppid());
                    logger.info("WeixinOauthController->oauth->跳转->page:{},openid:{}",page,openid);
                    StringBuilder url=new StringBuilder();
                    url.append(InternalResourceViewResolver.REDIRECT_URL_PREFIX );
                    url.append(jumpUrlValue);
                    if(urlAppendParam!=null){
                        url.append("?param="+urlAppendParam);
                    }
                    //url.append("?token=");
                   // url.append(MyEncryptUtil.encry(StringUtils.isBlank(this.WXGlobals.getTestOpenid())?openid:this.WXGlobals.getTestOpenid()));
                   /*if(json.containsKey("param") && json.getString("param")!=null){
                       url.append("&").append("param=").append(json.getString("param"));
                   }*/
                    logger.info("WeixinOauthController->oauth->跳转->url:{}",url.toString());
                   return url.toString();

                }
            }
        }

        // 所有请求都跳转到此页面上
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/html/main.html";
    }

    /**
     * 拉取用户信息
     *
     * @param access_token token
     * @param openid       微信 OPENID
     * @return 用户信息
     */
    private JSONObject userinfo(String access_token, String openid) {
        String requestUrl = USERINFO.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);
        return WeixinUtil.httpRequest(requestUrl, "GET", null);
    }

    public static void main(String[] args)throws Exception {
        Base32 base32 = new Base32();
        String abc = "{\"appid\":\"wxac4072fc723524ff\",\"page\":\"myself-center\"}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid","wxac4072fc723524ff");
        jsonObject.put("page","obn");
        jsonObject.put("param","m27123");
//        jsonObject.put("param","oNSHajg7OZ-K3yqzERRHOzudEm27123,oNSHajg7OZ-K3yqzERRHOzudEm26102");

        logger.info(abc);

        System.out.println(CommonUtil.base32Encode(jsonObject.toString()));
        System.out.println(CommonUtil.base32Encode(jsonObject.toString()).length());
        System.out.println(CommonUtil.base32Decode("PMRGC4DQNFSCEORCO54GINLCMEYWCYRTGA4GGMJZGA4GEIRMEJYGCZ3FEI5CE33QMVXC24TFMQWXAYLDNNSXIIT5"));
        System.out.println(CommonUtil.base32Decode("PMRGC4DQNFSCEORCO54GINLCMEYWCYRTGA4GGMJZGA4GEIRMEJYGCZ3FEI5CE5TPOVRWQZLSEJ6Q"));
        System.out.println(CommonUtil.base32Decode("PMRGC4DQNFSCEORCO54GINLCMEYWCYRTGA4GGMJZGA4GEIRMEJYGCZ3FEI5CE3LZONSWYZRNMNSW45DFOIRH2"));
        System.out.println(base32.encodeAsString(abc.getBytes()));
        abc = base32.encodeAsString(abc.getBytes()).replace("=", "");
        logger.info(abc);

        byte[] debytes = base32.decode(abc);
        logger.info(new String(debytes));
        //
        //String base64Token=BaseTokenUtil.createToken("oGb9J03naSTqAXJWLvWIYafTRvts");
 /*       String tokenStr= DateFormatUtils.format(new Date(), RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");
        String base64Token=Base64Utils.encodeToString(tokenStr.getBytes());
        base64Token=base64Token.replace("+","-").replace("/","_");
        Map cookieM=new HashMap();
        cookieM.put("tableNumber","001");
        cookieM.put("t",base64Token );
        BaseTokenUtil.putUserIdAndStoreId(base64Token,140,1);


        System.out.println(URLEncoder.encode(TydicDES.encodeValue(JSONObject.toJSONString(cookieM)),"utf-8"));*/


    }
}