package com.weixin.entity;

import java.util.Date;

/**
 * 微信摇一摇记录表
 * @author Jacky
 *
 */
public class ShakeRecord {
	private String id;
	/**微信openid */
	private String openid;
	/**活动名称 */
	private String name;
	/**访问来源 */
	private String src;
	/**门店 */
	private String shop;
	/**创建时间 */
	private Date create_date;
	public String getShop() {
		return shop;
	}
	public void setShop(String shop) {
		this.shop = shop;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
}
