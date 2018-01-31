package com.weixin.controller;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.weixin.core.pojo.AccessToken;
import com.weixin.services.WeixinInterfaceService;

/**
 * 提供给外部调用的微信接口
 * @author Jacky
 *
 */
@Controller
@RequestMapping(value = "weixin/cgi")
public class WeixinCGICtrl{

    @Autowired
    private WeixinInterfaceService weixinInterfaceService;

    /**
     * 返回accesstoken
     *
     * @param appid 应用ID
     * @return 公众号accesstoken信息
     */
    @ResponseBody
    @RequestMapping(value="accesstoken", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAccesstoken(String appid) {
    	JSONObject jsonObject = new JSONObject();
    	AccessToken at = weixinInterfaceService.getTokenByAppid(appid);
    	jsonObject.put("token", at.getToken());
        return jsonObject.toString();
    }

    /**
     * JS-SDK使用权限签名
     * @param appid 应用ID
     * @param url 当前网页的URL，不包含#及其后面部分
     * @return
     */
    @ResponseBody
    @RequestMapping(value="sign", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getSign(String appid,String url) {
    	JSONObject jsonObject = new JSONObject();
    	jsonObject = weixinInterfaceService.getSign(appid,url);
    	return jsonObject.toString();
    }

    /**
     * 查询微信用户信息
     *
     * @param openid 微信openid
     * @return 微信用户信息
     */
    @ResponseBody
    @RequestMapping(value="user", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserInfo(String appid,String openid) {
    	return weixinInterfaceService.userInfo(appid, openid).toString();
    }

    /**
     * 获取关注者列表
     * @param appid 应用id
     * @param openid 微信openid
     * @return
     */
    @ResponseBody
    @RequestMapping(value="alluser", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllUser(String appid,String openid) {
    	return weixinInterfaceService.getAllUser(appid, openid).toString();
    }

    /**
     * 获取自定义菜单
     * @param appid 应用id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="getmenu", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getMenu(String appid) {
    	return weixinInterfaceService.getMenu(appid).toString();
    }
    @ResponseBody
    @RequestMapping(value="getselfmenu", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getCurrentSelfMenu(String appid) {
    	return weixinInterfaceService.getCurrentSelfMenu(appid).toString();
    }
    /**
	 * 获取用户增减数据</br>
	 * 最大时间跨度 7天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getusersummary", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserSummary(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserSummary(appid,begin_date, end_date).toString();
    }
    /**
     * 获取累计用户数据</br>
     * 最大时间跨度 7天</br>
     * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
     * @param appid 应用ID
     * @param begin_date 开始日期
     * @param end_date 结束日期
     * @return
     */
    @ResponseBody
    @RequestMapping(value="getusercumulate", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserCumulate(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserCumulate(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文群发每日数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getarticlesummary", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getArticleSummary(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getArticleSummary(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文群发总数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getarticletotal", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getArticleTotal(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getArticleTotal(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文统计数据</br>
	 * 最大时间跨度 3天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getuserread", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserreAd(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserreAd(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文统计分时数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getuserreadhour", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserReadhour(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserReadhour(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文分享转发数据</br>
	 * 最大时间跨度 7天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getusershare", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserShare(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserShare(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取图文分享转发分时数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getusersharehour", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUserShareHour(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUserShareHour(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送概况数据</br>
	 * 最大时间跨度 7天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsg", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsg(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsg(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息分送分时数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsghour", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgHour(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgHour(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送周数据</br>
	 * 最大时间跨度 30天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsgweek", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgWeek(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgWeek(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送月数据</br>
	 * 最大时间跨度 30天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsgmonth", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgMonth(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgMonth(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送分布数据</br>
	 * 最大时间跨度 15天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsgdist", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgDist(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgDist(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送分布周数据</br>
	 * 最大时间跨度 30天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsgdistweek", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgDistWeek(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgDistWeek(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取消息发送分布月数据</br>
	 * 最大时间跨度 30天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getupstreammsgdistmonth", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getUpStreamMsgDistMonth(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getUpStreamMsgDistMonth(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取接口分析数据</br>
	 * 最大时间跨度 30天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getinterfacesummary", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getInterfaceSummary(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getInterfaceSummary(appid,begin_date, end_date).toString();
    }
    /**
	 * 获取接口分析分时数据</br>
	 * 最大时间跨度 1天</br>
	 * 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param appid 应用ID
	 * @param begin_date 开始日期
	 * @param end_date 结束日期
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getinterfacesummaryhour", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getInterfaceSummaryHour(String appid,String begin_date,String end_date) {
    	return weixinInterfaceService.getInterfaceSummaryHour(appid,begin_date, end_date).toString();
    }
    /**
     * 获取多媒体文件
     * @param appid 应用id
     * @param mediaid 媒体文件id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="getmedia", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getMedia(String appid,String mediaid) {
    	return weixinInterfaceService.getMedia(appid,mediaid).toString();
    }
    
    /**
	 * 创建二维码ticket
	 * @param appid 应用id
	 * @param expire_seconds 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
	 * @param action_name 二维码类型，QR_SCENE为临时,QR_LIMIT_SCENE为永久,QR_LIMIT_STR_SCENE为永久的字符串参数值
	 * @param scene_id 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 * @param scene_str 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64，仅永久二维码支持此字段
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="getqrcode", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getQrcode(String appid,String expire_seconds,
			String action_name,String scene_id,String scene_str) {
    	return weixinInterfaceService.getQrcode(appid,expire_seconds,
    			action_name,scene_id,scene_str).toString();
    }
    /**
     * 获取所有客服账号
     * @param appid 应用id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="getkflist", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getKfList(String appid) {
    	return weixinInterfaceService.getKfList(appid).toString();
    }

}
