package com.weixin.util;

import com.alibaba.fastjson.JSONObject;
import com.weixin.entity.Pubweixin;
import com.weixin.core.common.WeChatSystemContext;
import com.weixin.core.pojo.AccessToken;
import com.weixin.dao.PubweixinMapper;
import com.weixin.entity.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.*;

/**
 * 公众平台通用接口工具类
 * 
 */
@Service
public class WeixinUtil {
    private static final Logger logger = Logger.getLogger(WeixinUtil.class);
	
	@Autowired
	private PubweixinMapper pubweixinMapper;

	static Set<AccessToken> AccessTokenSet = new HashSet<AccessToken>();

	// 获取access_token的接口地址（GET） 限200（次/天）
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//获得jsapi_ticket
	public final static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

	/**
	 * 获取access_token
	 *
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	public static AccessToken getAccessToken(String appid, String appsecret) {
		AccessToken accessToken = null;

		String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);

		//首先判断本地有无记录，记录是否过期 7200s
		boolean isExpired = WeChatSystemContext.getInstance().isExpired();
		accessToken = new AccessToken();
		if(isExpired) {
			JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
			accessToken.setToken(jsonObject.getString("access_token"));
			//取jsapi_ticket
			JSONObject jsapi_ticketjson = httpRequest(jsapi_ticket_url.replace("ACCESS_TOKEN", accessToken.getToken()), "GET", null);
			accessToken.setTicket(jsapi_ticketjson.getString("ticket"));
			//记录到配置 access_token 当前时间
			WeChatSystemContext.getInstance().saveLocalAccessonToke(accessToken.getToken());
			return accessToken;
		} else {
			//从配置中直接获取access_token
			accessToken.setToken(WeChatSystemContext.getInstance().getAccessToken());
			return accessToken;
		}
	}

	/**
	 * 根据公众号取相关的token
	 * get_customer_token
	 * @return AccessToken 公众号对应token
	 */
	public Set<AccessToken> get_customer_token() {
		//查询正常状态的微信公众号
		List<Pubweixin> temp = pubweixinMapper.select();
		//查询已经删除的微信公众号
		List<Pubweixin> samePub = new ArrayList<Pubweixin>();
		long time = new Date().getTime();
		//判断存在内存的AccessTokenSet与数据库有效的公众号长度不相等，添加新公众号accessToken
		if (temp.size() > 0 && temp.size() != AccessTokenSet.size()){
			for (Pubweixin pw : temp) {
				for (AccessToken accessToken : AccessTokenSet) {
					if(pw.getAppid().equals(accessToken.getAppid())){
						samePub.add(pw);
					}
				}
			}
			temp.removeAll(samePub);
			for (Pubweixin pw : temp) {
				AccessToken accessToken = new AccessToken(pw.getAppid(),pw.getAppsecret());
				AccessTokenSet.add(accessToken);
			}
		}
		//添加token和失效时间
		for (AccessToken accessToken : AccessTokenSet) {
			//初始化TOKEN
			if (accessToken.getToken() == null && accessToken.getExpiresIn() == 0) {
				get_token(accessToken);
				//超时重置TOKEN
			} else if(accessToken.getExpiresIn()/ 1000 + 7200 < time / 1000){
				get_token(accessToken);
			}
		}
		return AccessTokenSet;
	}
	/**
	 * 调用微信接口请求返回token
	 * get_token
	 */
	public static void get_token(AccessToken accessToken) {
		String appid = accessToken.getAppid();
		String appsecret = accessToken.getSecret();
		String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		if (jsonObject.containsKey("access_token")) {
			accessToken.setToken(jsonObject.getString("access_token"));
			accessToken.setExpiresIn(new Date().getTime());
			//取jsapi_ticket
			JSONObject jsapi_ticketjson = httpRequest(jsapi_ticket_url.replace("ACCESS_TOKEN", accessToken.getToken()), "GET", null);
			if (jsapi_ticketjson.containsKey("ticket")) {
				accessToken.setTicket(jsapi_ticketjson.getString("ticket"));
			}
		}
	}

	/**
	 * 解析微信用户资料json数据
	 */
	public UserInfo parseJsonToUserInfo(JSONObject jsonObject, String openid, String appid) {
		UserInfo user = new UserInfo();
		try {
			user.setOpenid(openid);
			user.setAppid(appid);
			if(jsonObject.containsKey("city")){
				user.setCity(jsonObject.getString("city"));
			}
			if(jsonObject.containsKey("country")){
				user.setCountry(jsonObject.getString("country"));
			}
			if(jsonObject.containsKey("headimgurl")){
				user.setHeadimgurl(jsonObject.getString("headimgurl"));
			}
			if(jsonObject.containsKey("language")){
				user.setLanguage(jsonObject.getString("language"));
			}
			if(jsonObject.containsKey("province")){
				user.setProvince(jsonObject.getString("province"));
			}
			if(jsonObject.containsKey("sex")){
				user.setSex(jsonObject.getString("sex"));
			}
			if(jsonObject.containsKey("nickname")){
				user.setNickname(EmojiUtil.resolveToByteFromEmoji(jsonObject.getString("nickname")));
//                user.setNickname(jsonObject.getString("nickname"));
			}
			if(jsonObject.containsKey("subscribe")){
				user.setSubscribe(jsonObject.getString("subscribe"));
			}else{
				user.setSubscribe("0");
			}
			if (jsonObject.containsKey("subscribe_time")) {
				user.setSubscribe_time(new Date(Long.valueOf(jsonObject.getString("subscribe_time")) * 1000));
			}
			if(jsonObject.containsKey("remark")){
				user.setRemark(jsonObject.getString("remark"));
			}
			if(jsonObject.containsKey("unionid")){
				user.setUnionid(jsonObject.getString("unionid"));
			}
			if(jsonObject.containsKey("tagid_list")){
				user.setTagid_list(jsonObject.getString("tagid_list").replace("[","").replace("]",""));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return user;
	}


	/**
	 * 发起https请求并获取结果
	 *
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			logger.error("Weixin server connection timed out.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return jsonObject;
	}
	/**
	 * 获取文件类型
	 * @param contentType 文件类型
	 * @return 文件类型
	 */
	public static String getFileEndWitsh(String contentType) {
		String fileEndWitsh = "";
		if ("image/jpeg".equals(contentType))
			fileEndWitsh = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileEndWitsh = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileEndWitsh = ".amr";
		else if ("video/mp4".equals(contentType))
			fileEndWitsh = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileEndWitsh = ".mp4";
		return fileEndWitsh;
	}

	/**
	 * 重置AccessToken
	 * @param appid 应用id
	 */
	public void resetToken(String appid){
		//添加token和失效时间
		for (AccessToken accessToken : AccessTokenSet) {
			//重置TOKEN
			if (accessToken.getAppid().equals(appid)) {
				get_token(accessToken);
			}
		}
	}
}