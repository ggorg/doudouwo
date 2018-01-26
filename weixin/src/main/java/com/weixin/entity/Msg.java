package com.weixin.entity;

import java.util.Date;

/**
 * 消息管理
 * @author Jacky
 *
 */
public class Msg{
	private static final long serialVersionUID = 1L;
	
	private String id;
	/** 应用ID*/
	private String appid;
	/** 用户OPENID*/
	private String openid;
	/** 收藏标志（1收藏，0未收藏）*/
	private String collect;
	/** 消息类型（1文本，2图片，3视频，4小视频,5音频，0其它）*/
	private String msgtype;
	/** 文本消息*/
	private String content;
	/** 文件id*/
	private String fileid;
	/** 文件路径*/
	private String filepath;
	/** 创建时间*/
	private Date create_date;
	/** 备注*/
	private String remark;
	/** 回复标志（1已回复，0未回复）*/
	private String reply;
	/** 官方标志（1官方，0非官方）*/
	private String official;
	/** 微信用户信息*/
	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getFilepath() {
		return filepath;
	}
	
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public String getReply() {
		return reply;
	}
	
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOfficial() {
		return official;
	}
	
	public void setOfficial(String official) {
		this.official = official;
	}

}
