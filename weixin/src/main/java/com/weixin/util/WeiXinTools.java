package com.weixin.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;

public class WeiXinTools {
	private static String ticket;
	private static String appid;
	public static void initTicket(String inputTicket,String inputAppid){
		ticket=inputTicket;
		appid=inputAppid;
	}
	/*
	 * 
	 * @param url  当前页面的url 比如：http://wwww.youryour.cn/TestChat/test 或者http://wwww.youryour.cn/TestChat/test?xx=xx
	 * @param jsApiList 想调什么函数就加什么函数,并且以逗号隔开，比如openLocation,getLocation,closeWindow
	 * @return
	 */
	public static String initJssdk(String url,String jsApiList){
		
		String timestamp=(System.currentTimeMillis()/1000)+"";
		String nonceStr= RandomStringUtils.randomAlphabetic(16);
		StringBuilder builder=new StringBuilder();
		builder.append("jsapi_ticket=").append(ticket).append("&");
		builder.append("noncestr=").append(nonceStr).append("&");
		builder.append("timestamp=").append(timestamp).append("&");
		builder.append("url=").append(url);
		String shastr= DigestUtils.shaHex(builder.toString());
		builder=new StringBuilder();
		builder.append("<script type=\"text/javascript\" src=\"https://res.wx.qq.com/open/js/jweixin-1.2.0.js\"></script>");
		builder.append("<script type=\"text/javascript\">");
		builder.append("wx.config({");
		builder.append("debug:").append("false").append(",");
		builder.append("appId:'").append(appid).append("',");
		builder.append("timestamp:").append(timestamp).append(",");
		builder.append("nonceStr:'").append(nonceStr).append("',");
		builder.append("signature:'").append(shastr).append("',");
		builder.append("jsApiList:").append(Arrays.asList(jsApiList).toString().replaceAll("([a-zA-Z]+)", "'$1'"));
		builder.append("});");
	/*	wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: 'wxd5ba1ab308c1908b', // 必填，公众号的唯一标识
		    timestamp:<%=timestamp%> , // 必填，生成签名的时间戳
		    nonceStr: '<%=nonce%>', // 必填，生成签名的随机串
		    signature: '<%=shastr%>',// 必填，签名，见附录1
		    jsApiList: ['openLocation','getLocation','closeWindow'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});*/
		builder.append("</script>");



		return builder.toString();
	}

}
