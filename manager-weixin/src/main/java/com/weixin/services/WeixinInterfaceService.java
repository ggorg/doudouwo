package com.weixin.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.common.util.HttpUtil;
import com.weixin.core.pojo.Menu;
import com.weixin.core.pojo.AccessToken;
import com.weixin.dao.RedPackRecordMapper;
import com.weixin.entity.RedPackRecord;
import com.weixin.entity.UserInfo;
import com.weixin.util.SignUtil;
import com.weixin.util.WeixinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 微信接口调用
 * @author Jacky
 *
 */
@Service
public class WeixinInterfaceService {
	@Autowired
	private WeixinUtil weixinUtil;
	@Autowired
	private SendredpackService sendredpackService;
	@Autowired
	private RedPackRecordMapper redPackRecordMapper;

	/** 获取ACCESS_TOKEN URL */
	private final static String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	/** 客服接口-发消息 */
	private final static String MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	/** 获取关注者列表 */
	private final static String ALLUSER_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
	/** 自定义菜单*/
	private final static String MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	/** 自定义菜单查询接口*/
	private final static String MENU_QUERY_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	/** 获取自定义菜单配置接口*/
	private final static String GET_CURRENT_SELFMENU_INFO_URL = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN";
	/** 自定义菜单删除接口*/
	private final static String DELETEMENU_QUERY_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/** 获取用户增减数据*/
	private final static String GETUSERSUMMARY_URL = "https://api.weixin.qq.com/datacube/getusersummary?access_token=ACCESS_TOKEN";
	/** 获取累计用户数据*/
	private final static String GETUSERCUMULATE_URL = "https://api.weixin.qq.com/datacube/getusercumulate?access_token=ACCESS_TOKEN";
	/** 获取图文群发每日数据*/
	private final static String GETARTICLESUMMARY_URL = "https://api.weixin.qq.com/datacube/getarticlesummary?access_token=ACCESS_TOKEN";
	/** 获取图文群发总数据*/
	private final static String GETARTICLETOTAL_URL = "https://api.weixin.qq.com/datacube/getarticletotal?access_token=ACCESS_TOKEN";
	/** 获取图文统计数据*/
	private final static String GETUSERREAD_URL = "https://api.weixin.qq.com/datacube/getuserread?access_token=ACCESS_TOKEN";
	/** 获取图文统计分时数据*/
	private final static String GETUSERREADHOUR_URL = "https://api.weixin.qq.com/datacube/getuserreadhour?access_token=ACCESS_TOKEN";
	/** 获取图文分享转发数据*/
	private final static String GETUSERSHARE_URL = "https://api.weixin.qq.com/datacube/getusershare?access_token=ACCESS_TOKEN";
	/** 获取图文分享转发分时数据*/
	private final static String GETUSERSHAREHOUR_URL = "https://api.weixin.qq.com/datacube/getusersharehour?access_token=ACCESS_TOKEN";
	/** 获取消息发送概况数据*/
	private final static String GETUPSTREAMMSG_URL = "https://api.weixin.qq.com/datacube/getupstreammsg?access_token=ACCESS_TOKEN";
	/** 获取消息分送分时数据*/
	private final static String GETUPSTREAMMSGHOUR_URL = "https://api.weixin.qq.com/datacube/getupstreammsghour?access_token=ACCESS_TOKEN";
	/** 获取消息发送周数据*/
	private final static String GETUPSTREAMMSGWEEK_URL = "https://api.weixin.qq.com/datacube/getupstreammsgweek?access_token=ACCESS_TOKEN";
	/** 获取消息发送月数据*/
	private final static String GETUPSTREAMMSGMONTH_URL = "https://api.weixin.qq.com/datacube/getupstreammsgmonth?access_token=ACCESS_TOKEN";
	/** 获取消息发送分布数据*/
	private final static String GETUPSTREAMMSGDIST_URL = "https://api.weixin.qq.com/datacube/getupstreammsgdist?access_token=ACCESS_TOKEN";
	/** 获取消息发送分布周数据*/
	private final static String GETUPSTREAMMSGDISTWEEK_URL = "https://api.weixin.qq.com/datacube/getupstreammsgdistweek?access_token=ACCESS_TOKEN";
	/** 获取消息发送分布月数据*/
	private final static String GETUPSTREAMMSGDISTMONTH_URL = "https://api.weixin.qq.com/datacube/getupstreammsgdistmonth?access_token=ACCESS_TOKEN";
	/** 获取接口分析数据*/
	private final static String GETINTERFACESUMMARY_URL = "https://api.weixin.qq.com/datacube/getinterfacesummary?access_token=ACCESS_TOKEN";
	/** 获取接口分析分时数据*/
	private final static String GETINTERFACESUMMARYHOUR_URL = "https://api.weixin.qq.com/datacube/getinterfacesummaryhour?access_token=ACCESS_TOKEN";
	/** 下载多媒体文件接口*/
	private final static String MEDIA_URL = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	/** 创建场景二维码 */
	private final static String QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	/** 长链接转短链接接口 */
	private final static String SHORTURL_URL = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=ACCESS_TOKEN";
	/** 获取所有客服账号 */
	private final static String GETKFLIST_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN";
	/** 修改用户备注*/
	private final static String UPDATEREMARK_URL = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=ACCESS_TOKEN";
	/** 获得模板ID*/
	private final static String API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
	/** 创建标签*/
	private final static String TAGS_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN";
	/** 获取公众号已创建的标签*/
	private final static String TAGS_GET_URL = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
	/** 编辑标签*/
	private final static String TAGS_UPDATE_URL = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=ACCESS_TOKEN";
	/** 删除标签*/
	private final static String TAGS_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=ACCESS_TOKEN";
	/** 获取标签下粉丝列表*/
	private final static String TAGS_USER_URL = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=ACCESS_TOKEN";
	/** 批量为用户打标签*/
	private final static String BATCHTAGGING_MEMBERS_URL = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=ACCESS_TOKEN";
	/** 批量为用户取消打标签*/
	private final static String BATCHUNTAGGING_MEMBERS_URL = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=ACCESS_TOKEN";
	/** 上传图文消息内的图片获取URL*/
	private final static String MEDIA_UPLOADIMG_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	/** */
	private final static String COMPONENT_URL = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
	/**
	 * 微信用户资料
	 * @param appid 应用id
	 * @param openid 用户原始id
	 * @return UserInfo
	 */
	public UserInfo getUserInfo(String appid, String openid) {
		UserInfo user = new UserInfo();
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		if (at != null) {
			String requestUrl = USERINFO_URL.replace("ACCESS_TOKEN", at.getToken()).replace("OPENID", openid);
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
			user = weixinUtil.parseJsonToUserInfo(jsonObject, openid, appid);
			/*
			if(user.getNickname()!=null){
				user.setNickname(EmojiUtil.resolveToByteFromEmoji(user.getNickname()));
			}
			*/
		}
		return user;
	}

	/**
	 * 微信用户资料
	 * @param appid 应用ID
	 * @param openid 应用密钥
	 * @return JSONObject
	 */
	public JSONObject userInfo(String appid, String openid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = USERINFO_URL.replace("ACCESS_TOKEN", at.getToken()).replace("OPENID", openid);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		}
		return jsonObject;
	}


	/**
	 * 数据库已经登记过该公众号信息
	 * 获取AccessToken
	 * @param appid 应用id
	 * @return 存在则返回AccessToken，否则返回null
	 */
	public AccessToken getTokenByAppid(String appid) {
		Set<AccessToken> AccessTokenSet = weixinUtil.get_customer_token();
		for (AccessToken at : AccessTokenSet) {
			if (at.getAppid().equals(appid)) {
				return at;
			}
		}
		return null;
	}
	/**
	 * JS-SDK使用权限签名
	 * @param appid 应用ID
	 * @param url 当前网页的URL，不包含#及其后面部分
	 * @return
	 */
	public JSONObject getSign(String appid,String url){
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || url == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			jsonObject = SignUtil.getSign(at.getTicket(),url);
		}
		return jsonObject;
	}
	/**
	 * 向用户发送消息
	 * @param appid 应用id
	 * @param content 内容
	 * @return json 结果
	 */
	public JSONObject sendMessage(String appid,String content) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = MESSAGE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", content);
		}
		return jsonObject;
	}

	/**
	 * 发送现金红包方法
	 * @param appid 应用ID
	 * @param openid 发送给指定微信用户的openid
	 * @param totalAmount 红包金额，单位分
	 * @param Send_name 商户名称(红包发送者名称)
	 * @param wishing 红包祝福语
	 * @param Act_name 活动名称
	 * @param setRemark 备注
	 * @return JSON数据
	wxappid 应用ID</br>
	mch_id 商户号</br>
	mch_billno 商户订单号</br>
	total_amount  付款金额，单位分</br>
	re_openid 用户openid</br>
	err_code 错误代码</br>
	return_msg 返回信息</br>
	result_code 业务结果</br>
	err_code_des 错误代码描述</br>
	return_code 返回状态码</br>
	create_date 创建时间</br>
	send_time 发放成功时间</br>
	send_listid 微信单号</br>
	 */
	public JSONObject sendRedPack(String appid, String openid, int totalAmount, String Send_name, String wishing, String Act_name, String setRemark) {
		JSONObject jsonObject = sendredpackService.sendRedPack(appid, openid, totalAmount, Send_name, wishing, Act_name, setRemark);
		// 解析微信红包json数据
		RedPackRecord redPackRecord = sendredpackService.parseJsonToRedPackRecord(jsonObject);
		if (!redPackRecord.getReturnCode().equals("SUCCESS") || !redPackRecord.getResultCode().equals("SUCCESS")) {
			redPackRecord.setAppid(appid);
		}
		redPackRecord.setCreateDate(new Date());
		redPackRecord.setSendName(Send_name);
		redPackRecord.setWishing(wishing);
		redPackRecord.setActName(Act_name);
		redPackRecord.setRemark(setRemark);
		redPackRecordMapper.insert(redPackRecord);
		jsonObject.put("id", redPackRecord.getId());
		return jsonObject;
	}

	/**
	 * 获取关注者列表
	 * @param appid 应用ID
	 * @param openid 列表的后一个用户的OPENID
	 * @return JSON数据</br>
	 * total 关注该公众账号的总用户数;</br>
	 * count 拉取的OPENID个数，最大值为10000;</br>
	 * data 列表数据，OPENID的列表;
	 */
	public JSONObject getAllUser(String appid, String openid) {
		openid = openid == null ? "" : openid;
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = ALLUSER_URL.replace("ACCESS_TOKEN", at.getToken()).replace("NEXT_OPENID", openid);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		}
		return jsonObject;
	}

	/**
	 * 创建菜单
	 *
	 * @param menu 菜单实例
	 * @param appid 应用ID
	 * @return 0表示成功，其他值表示失败
	 */
	public JSONObject createMenu(String appid,Menu menu) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		// 拼装创建菜单的url
		String requestUrl = MENU_URL.replace("ACCESS_TOKEN", at.getToken());
		// 将菜单对象转换成json字符串
		String jsonMenu = JSONObject.toJSONString(menu).toString();
		// 调用接口创建菜单
		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonMenu);
		return jsonObject;
	}
	/**
	 * 自定义菜单查询接口
	 * @param appid 应用ID
	 * @return
	 */
	public JSONObject getMenu(String appid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		// 拼装创建菜单的url
		String requestUrl = MENU_QUERY_URL.replace("ACCESS_TOKEN", at.getToken());
		// 调用接口查询菜单
		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		return jsonObject;
	}

	/**
	 * 获取自定义菜单配置接口
	 * @param appid 应用id
	 * @return
	 */
	public JSONObject getCurrentSelfMenu(String appid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		// 拼装创建菜单的url
		String requestUrl = GET_CURRENT_SELFMENU_INFO_URL.replace("ACCESS_TOKEN", at.getToken());
		// 调用接口查询菜单
		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		return jsonObject;
	}

	/**
	 * 删除自定义菜单
	 * @param appid 应用id
	 * @return
	 */
	public JSONObject deleteMenu(String appid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		// 拼装创建菜单的url
		String requestUrl = DELETEMENU_QUERY_URL.replace("ACCESS_TOKEN", at.getToken());
		// 调用接口查询菜单
		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		return jsonObject;
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
	public JSONObject getUserSummary(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERSUMMARY_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUserCumulate(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERCUMULATE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getArticleSummary(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETARTICLESUMMARY_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getArticleTotal(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETARTICLETOTAL_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUserreAd(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERREAD_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUserReadhour(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERREADHOUR_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUserShare(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERSHARE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUserShareHour(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUSERSHAREHOUR_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsg(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSG_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgHour(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGHOUR_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgWeek(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGWEEK_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgMonth(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGMONTH_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgDist(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGDIST_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgDistWeek(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGDISTWEEK_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getUpStreamMsgDistMonth(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETUPSTREAMMSGDISTMONTH_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getInterfaceSummary(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETINTERFACESUMMARY_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
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
	public JSONObject getInterfaceSummaryHour(String appid,String begin_date,String end_date) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || begin_date == null || end_date == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETINTERFACESUMMARYHOUR_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("begin_date", begin_date);
			jsonObject.put("end_date", end_date);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}
	/**
	 * 获取多媒体文件
	 * @param appid 应用id
	 * @param mediaid 媒体文件ID
	 * @return
	 */
	@SuppressWarnings("static-access")
	public JSONObject getMedia(String appid,String mediaid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null || mediaid == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = MEDIA_URL.replace("ACCESS_TOKEN", at.getToken().replace("MEDIA_ID", mediaid));
			jsonObject = jsonObject.parseObject(HttpUtil.doGet(requestUrl));
		}
		return jsonObject;
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
	public JSONObject getQrcode(String appid,String expire_seconds,
								String action_name,String scene_id,String scene_str) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonObject3 = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = QRCODE_URL.replace("ACCESS_TOKEN", at.getToken());
			if(expire_seconds != null && !"".equals(expire_seconds)){
				jsonObject.put("expire_seconds", expire_seconds);
			}
			jsonObject.put("action_name", action_name);
			if(scene_id !=null && !"".equals(scene_id)){
				jsonObject3.put("scene_id", scene_id);
			}
			if(scene_str !=null && !"".equals(scene_str)){
				jsonObject3.put("scene_str", scene_str);
			}
			jsonObject2.put("scene", jsonObject3);
			jsonObject.put("action_info", jsonObject2);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}
	/**
	 * 长链接转短链接接口
	 * @param appid 应用id
	 * @param long_url 长链接
	 * @return
	 */
	public JSONObject getShortUrl(String appid,String long_url) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = SHORTURL_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("action", "long2short");
			jsonObject.put("long_url", long_url);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}
	/**
	 * 获取所有客服账号
	 * @param appid 应用id
	 * @return
	 */
	public JSONObject getKfList(String appid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = GETKFLIST_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		}
		return jsonObject;
	}
	/**
	 * 设置用户备注名
	 * @param appid 应用id
	 * @parma openid 用户id
	 * @param remark 新的备注名，长度必须小于30字符
	 * @return
	 */
	public JSONObject updateRemark(String appid, String openid, String remark) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = UPDATEREMARK_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("openid", openid);
			jsonObject.put("remark", remark);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}
	/**
	 * 获取媒体文件
	 * @param appid 应用appid
	 * @param mediaId 媒体文件id
	 * @param savePath 文件在服务器上的存储路径
	 * */
	public JSONObject downloadMedia(String appid, String mediaId, String savePath) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String filePath = null;
			String requestUrl = MEDIA_URL.replace("ACCESS_TOKEN", at.getToken().replace("MEDIA_ID", mediaId));
			try {
				URL url = new URL(requestUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setRequestMethod("GET");

				if (!savePath.endsWith("/")) {
					savePath += "/";
				}
				// 根据内容类型获取扩展名
				String fileExt = WeixinUtil.getFileEndWitsh(conn.getHeaderField("Content-Type"));
				// 将mediaId作为文件名
				filePath = savePath + mediaId + fileExt;

				BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				byte[] buf = new byte[8096];
				int size = 0;
				while ((size = bis.read(buf)) != -1)
					fos.write(buf, 0, size);
				fos.close();
				bis.close();

				conn.disconnect();
				String info = String.format("下载媒体文件成功，filePath=" + filePath);
				System.out.println(info);
				jsonObject.put("filename", mediaId + fileExt);
			} catch (Exception e) {
				filePath = null;
				String error = String.format("下载媒体文件失败：%s", e);
				System.out.println(error);
			}
		}
		return jsonObject;
	}

	/**
	 * 获得模板ID
	 * @param appid 应用id
	 * @param templateIdShort 模板库中模板的编号，有“TM**”和“OPENTMTM**”等形式
	 * @return
	 */
	public JSONObject apiAddTemplate(String appid, String templateIdShort) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = API_ADD_TEMPLATE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("template_id_short", templateIdShort);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		//token过期重置token
		if(jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("40001")){
			// 重置token
			weixinUtil.resetToken(appid);
			apiAddTemplate(appid, templateIdShort);
		}
		return jsonObject;
	}

	/**
	 * 创建标签
	 * @param appid 应用id
	 * @param name 标签名（30个字符以内）
	 * @return {   "tag":{ "id":134,//标签id "name":"广东"   } }
	 */
	public JSONObject tagsCreate(String appid, String name) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		JSONObject tagJsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = TAGS_CREATE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("name", name);
			tagJsonObject.put("tag",jsonObject);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", tagJsonObject.toString());
		}
		return jsonObject;
	}

	/**
	 * 获取公众号已创建的标签
	 * @param appid 应用id
	 * @return {   "tags":[{       "id":1,       "name":"每天一罐可乐星人",       "count":0 //此标签下粉丝数 },{   "id":2,   "name":"星标组",   "count":0 },{   "id":127,   "name":"广东",   "count":5 }   ] }
	 */
	public JSONObject tagsGet(String appid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = TAGS_GET_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
		}
		return jsonObject;
	}

	/**
	 * 编辑标签
	 * @param appid 应用id
	 * @param id 标签编号
	 * @param name 标签名（30个字符以内）
	 * @return {   "errcode":0,   "errmsg":"ok" }
	 */
	public JSONObject tagsUpdate(String appid,int id, String name) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		JSONObject tagJsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = TAGS_UPDATE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("id", id);
			jsonObject.put("name", name);
			tagJsonObject.put("tag",jsonObject);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", tagJsonObject.toString());
		}
		return jsonObject;
	}
	/**
	 * 删除标签
	 * @param appid 应用id
	 * @param id 标签编号
	 * @return {   "errcode":0,   "errmsg":"ok" }
	 */
	public JSONObject tagsDelete(String appid,int id) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		JSONObject tagJsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = TAGS_DELETE_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("id", id);
			tagJsonObject.put("tag",jsonObject);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", tagJsonObject.toString());
		}
		return jsonObject;
	}

	/**
	 * 获取标签下粉丝列表
	 * @param appid 应用id
	 * @param tagid 标签id
	 * @param next_openid 第一个拉取的OPENID，不填默认从头开始拉取
	 * @return
	 * {   "count":2,//这次获取的粉丝数量
	"data":{//粉丝列表
	"openid":[
	"ocYxcuAEy30bX0NXmGn4ypqx3tI0",
	"ocYxcuBt0mRugKZ7tGAHPnUaOW7Y"  ]
	},
	"next_openid":"ocYxcuBt0mRugKZ7tGAHPnUaOW7Y"//拉取列表最后一个用户的openid
	}
	 */
	public JSONObject tagsUser(String appid,int tagid,String next_openid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = TAGS_USER_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("tagid", tagid);
			jsonObject.put("next_openid", next_openid);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}

	/**
	 * 批量为用户打标签s
	 * @param appid 应用id
	 * @param tagid 标签id
	 * @param openid 用户openid List
	 * @return {
	"errcode":0,
	"errmsg":"ok"
	}
	 */
	public JSONObject batchTaggingMembers(String appid,int tagid,List<String> openid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = BATCHTAGGING_MEMBERS_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("openid_list", openid.toArray());
			jsonObject.put("tagid", tagid);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}

	/**
	 * 批量为用户取消标签
	 * @param appid
	 * @param tagid
	 * @param openid
	 * @return {
	"errcode":0,
	"errmsg":"ok"
	}
	 */
	public JSONObject batchUnTaggingMembers(String appid,int tagid,List<String> openid) {
		// 调用接口获取access_token
		AccessToken at = this.getTokenByAppid(appid);
		JSONObject jsonObject = new JSONObject();
		if (at == null) {
			jsonObject.put("retCode", -1);
			jsonObject.put("retMsg", "参数有误");
		} else {
			String requestUrl = BATCHUNTAGGING_MEMBERS_URL.replace("ACCESS_TOKEN", at.getToken());
			jsonObject.put("openid_list", openid.toArray());
			jsonObject.put("tagid", tagid);
			jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", jsonObject.toString());
		}
		return jsonObject;
	}

}