package com.weixin.core.pojo;

/**
 * 微信通用接口凭证
 * 
 */
public class AccessToken {
	//appid
	private String appid;
	//secret
	private String secret;
	//公众号用于调用微信JS接口的临时票据
	private String ticket;
	// 获取到的凭证
	private String token;
	// 凭证有效时间，单位：秒
	private long expiresIn;
	
	public AccessToken() {
		
	}
	
	public AccessToken(String appid,String secret) {
		this.appid = appid;
		this.secret = secret;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getTicket() {
		return ticket;
	}
	
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	@Override
	public String toString() {
		return "AccessToken [appid=" + appid
				+ ", secret=" + secret + ", token=" + token + ", expiresIn="
				+ expiresIn + "]";
	}
}