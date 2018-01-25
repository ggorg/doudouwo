package weixin.core.common;

import java.util.Date;

/**
 * 存储access_token的单例类
 * @author Jacky
 * 2015年3月25日
 */
public class WeChatSystemContext {
	private String accessToken;// 接口访问凭据
	private String ticket;//调用微信JS接口的临时票据

	private long createTime;// 接口访问凭据创建时间，理论上是2小时后过期

	static class AccessTokenHolder {
		static WeChatSystemContext instance = new WeChatSystemContext();
	}

	public static WeChatSystemContext getInstance() {
		return AccessTokenHolder.instance;
	}

	// 是否过期
	public boolean isExpired() {
		long time = new Date().getTime();
		// 如果当前记录时间为0
		if (this.createTime <= 0) {
			return true;
		}
		// 判断记录时间是否超过7200s
		if (this.createTime / 1000 + 7200 < time / 1000) {
			return true;
		}
		return false;
	}

	// 记录接口访问凭证
	public void saveLocalAccessonToke(String accessToken) {
		this.accessToken = accessToken;
		this.createTime = new Date().getTime();
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getCreateTime() {
		return createTime;
	}
	public String getTicket() {
		return ticket;
	}
	
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
