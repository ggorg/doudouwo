package com.weixin.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weixin.util.MessageUtil;
import com.weixin.core.common.UUIDHexGenerator;
import com.weixin.dao.RedPackMapper;
import com.weixin.entity.RedPack;
import com.weixin.entity.RedPackRecord;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 发送红包类
 * @author Jacky
 *
 */
@Service
public class SendredpackService {
	private static final Logger logger = Logger.getLogger("Sendredpack");
	@Autowired
	private RedPackMapper redPackMapper;
	
	private static final String REDPACK_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
	/**
	 * 签名字符串
	 * 
	 * @param text
	 *            需要签名的字符串
	 * @param key
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *            需要签名的字符串
	 * @param sign
	 *            签名结果
	 * @param key
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static boolean verify(String text, String sign, String key,
			String input_charset) {
		text = text + key;
		String mysign = DigestUtils
				.md5Hex(getContentBytes(text, input_charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
					+ charset);
		}
	}

	/**
	 * 生成6位或10位随机数 param codeLength(多少位)
	 * 
	 * @return
	 */
	private String createCode(int codeLength) {
		String code = "";
		for (int i = 0; i < codeLength; i++) {
			code += (int) (Math.random() * 9);
		}
		return code;
	}

	@SuppressWarnings("unused")
	private static boolean isValidChar(char ch) {
		if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')
				|| (ch >= 'a' && ch <= 'z'))
			return true;
		if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
			return true;// 简体中文汉字编码
		return false;
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	/**
	 * 发送现金红包方法
	 * @param appid 应用ID
	 * @param opendid 发送给指定微信用户的openid
	 * @param totalAmount 红包金额，单位分
	 * @param Send_name 商户名称(红包发送者名称)
	 * @param wishing 红包祝福语
	 * @param Act_name 活动名称
	 * @param setRemark 备注
	 * @return JSONObject
	 */
	public JSONObject sendRedPack(String appid,String opendid,int totalAmount,String Send_name,String wishing,String Act_name,String setRemark){
		JSONObject retJson = new JSONObject();
		String return_code = "";
		String return_msg ="";
		if(appid.equals("")||opendid.equals("")||wishing.equals("")||Act_name.equals("")||setRemark.equals("")){
			return_code = "FAIL";
			return_msg = "参数有误";
		}
		if(totalAmount<100 || totalAmount >20000 ){
			return_code = "FAIL";
			return_msg = "单个红包金额介于[1.00元，200.00元]之间";
		}
		//查询出红包配置
		RedPack redPack = redPackMapper.selectByappid(appid);
		if(redPack==null){
			return_code = "FAIL";
			return_msg = "红包配置为空！";
		}
		if(return_code.equals("FAIL")){
			retJson.put("return_code", return_code);
			retJson.put("return_msg", return_msg);
			return retJson;
		}
		String nonceStr = UUIDHexGenerator.getInstance().generate();
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String code = createCode(10);
		String mch_id = redPack.getMch_id();// 商户号
		RedPack sendRedPackPo = new RedPack();
		sendRedPackPo.setNonce_str(nonceStr);
		sendRedPackPo.setMch_billno(mch_id + today + code);
		sendRedPackPo.setMch_id(mch_id);
		sendRedPackPo.setWxappid(appid);
		sendRedPackPo.setSend_name(Send_name);
		sendRedPackPo.setRe_openid(opendid);
		sendRedPackPo.setTotal_amount(totalAmount);
		sendRedPackPo.setMin_value(totalAmount);
		sendRedPackPo.setMax_value(totalAmount);
		sendRedPackPo.setTotal_num(1);
		sendRedPackPo.setWishing(wishing);
		sendRedPackPo.setClient_ip(redPack.getClient_ip()); // IP
		sendRedPackPo.setAct_name(Act_name);
		sendRedPackPo.setRemark(setRemark);
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("nonce_str", sendRedPackPo.getNonce_str());
			sParaTemp.put("mch_billno", sendRedPackPo.getMch_billno());
			sParaTemp.put("mch_id", sendRedPackPo.getMch_id());
			sParaTemp.put("wxappid", sendRedPackPo.getWxappid());
			sParaTemp.put("nick_name", sendRedPackPo.getNick_name());
			sParaTemp.put("send_name", sendRedPackPo.getSend_name());
			sParaTemp.put("re_openid", sendRedPackPo.getRe_openid());
			sParaTemp.put("total_amount", String.valueOf(sendRedPackPo.getTotal_amount()));
			sParaTemp.put("min_value", String.valueOf(sendRedPackPo.getMin_value()));
			sParaTemp.put("max_value", String.valueOf(sendRedPackPo.getMax_value()));
			sParaTemp.put("total_num", String.valueOf(sendRedPackPo.getTotal_num()));
			sParaTemp.put("wishing", sendRedPackPo.getWishing());
			sParaTemp.put("client_ip", sendRedPackPo.getClient_ip());
			sParaTemp.put("act_name", sendRedPackPo.getAct_name());
			sParaTemp.put("remark", sendRedPackPo.getRemark());
			
			// 除去数组中的空值和签名参数
			Map<String, String> sPara = paraFilter(sParaTemp);
			String prestr = createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			String key = "&key="+redPack.getMch_key(); // 商户支付密钥
			String mysign = SendredpackService.sign(prestr, key, "utf-8").toUpperCase();
			
			sendRedPackPo.setSign(mysign);
			
			String respXml = MessageUtil.redPackMessageToXml(sendRedPackPo);
			
			// 打印respXml发现，得到的xml中有“__”不对，应该替换成“_”
			respXml = respXml.replace("__", "_");
			
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			// 此处为证书所放的绝对路径
			FileInputStream instream = new FileInputStream(new File(redPack.getCertificate_path())); 
			
			try {
				keyStore.load(instream, mch_id.toCharArray());
			} finally {
				instream.close();
			}
			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom()
					.loadKeyMaterial(keyStore, mch_id.toCharArray()).build();
			// Allow TLSv1 protocol only
			@SuppressWarnings("deprecation")
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext, new String[] { "TLSv1" }, null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
			try {
				HttpPost httpPost = new HttpPost(REDPACK_URL);
				StringEntity reqEntity = new StringEntity(respXml, "utf-8");
				// 设置类型
				reqEntity.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(reqEntity);
				CloseableHttpResponse response = httpclient.execute(httpPost);
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						// 从request中取得输入流
						InputStream inputStream = entity.getContent();
						// 读取输入流
						SAXReader reader = new SAXReader();
						Document document = reader.read(inputStream);
						// 得到xml根元素
						Element root = document.getRootElement();
						// 得到根元素的所有子节点
						@SuppressWarnings("unchecked")
						List<Element> elementList = root.elements();
						// 遍历所有子节点
						for (Element e : elementList)
							map.put(e.getName(), e.getText());
						// 释放资源
						inputStream.close();
						inputStream = null;
					}
					EntityUtils.consume(entity);
				} finally {
					response.close();
				}
			} finally {
				httpclient.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
            retJson.put("return_code", "FAIL");
            retJson.put("return_msg", "系统异常");
            return retJson;
		}
		return JSONObject.parseObject(JSON.toJSONString(map));
	}

	/**
	 * 解析JSON数据
	 * @param jsonObject 发送红包后返回的JSON数据
	 * @return RedPackRecord
	 */
	public RedPackRecord parseJsonToRedPackRecord(JSONObject jsonObject){
	    RedPackRecord redPackRecord = new RedPackRecord();
	    redPackRecord.setReturnCode(jsonObject.getString("return_code"));
	    redPackRecord.setReturnMsg(jsonObject.getString("return_msg"));
		//以下字段在return_code为SUCCESS的时候有返回
		if(jsonObject.containsKey("result_code")){
		    redPackRecord.setResultCode(jsonObject.getString("result_code"));
		}
		if(jsonObject.containsKey("err_code")){
		    redPackRecord.setErrCode(jsonObject.getString("err_code"));
		}
		if(jsonObject.containsKey("err_code_des")){
		    redPackRecord.setErrCodeDes(jsonObject.getString("err_code_des"));
		}
		//以下字段在return_code和result_code都为SUCCESS的时候有返回
		if(jsonObject.containsKey("mch_billno")){
		    redPackRecord.setMchBillno(jsonObject.getString("mch_billno"));
		}
		if(jsonObject.containsKey("mch_id")){
		    redPackRecord.setMchId(jsonObject.getString("mch_id"));
		}
		if(jsonObject.containsKey("wxappid")){
		    redPackRecord.setAppid(jsonObject.getString("wxappid"));
		}
		if(jsonObject.containsKey("re_openid")){
		    redPackRecord.setReOpenid(jsonObject.getString("re_openid"));
		}
		if(jsonObject.containsKey("total_amount")){
		    redPackRecord.setTotalAmount(jsonObject.getInteger("total_amount"));
		}
		if(jsonObject.containsKey("send_time")){
		    redPackRecord.setSendTime(jsonObject.getInteger("send_time"));
		}
		if(jsonObject.containsKey("send_listid")){
		    redPackRecord.setSendListid(jsonObject.getString("send_listid"));
		}
		return redPackRecord;
	}
	
	public static void main(String[] args) {
		String appid = "wxdbb574c269389282";
		String openid = "odbefs5aAe-yU6xQ5PlyxMXCTLNg";
		int totalAmount = 100;
		String Send_name = "贝尔康";
		String wishing = "wishing";
		String Act_name = "Act_name";
		String setRemark = "setRemark";
		System.out.println(new SendredpackService().sendRedPack(appid, openid, totalAmount,Send_name, wishing, Act_name, setRemark).toString());
		/*
		 *  executing requestPOST https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack HTTP/1.1
HTTP/1.1 200 OK
{"total_amount":"100","result_code":"FAIL","mch_id":"1254037201","mch_billno":"1254037201201511198772018748","err_code":"SYSTEMERROR","wxappid":"wxdbb574c269389282","err_code_des":"请求已受理，请稍后使用原单号查询发放结果","return_msg":"请求已受理，请稍后使用原单号查询发放结果","re_openid":"odbefs5aAe-yU6xQ5PlyxMXCTLNg","return_code":"FAIL"}

		 */
	}
}
