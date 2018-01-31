package com.weixin.entity;

import java.util.Date;

/**
 * 微信事件处理-回复规则
 * Created by Jacky on 2017/12/15.
 */
public class EventRule {
    /** 主键ID*/
    private int id;
    /** 公众号ID*/
    private String appid;
    /** 标题*/
    private String title;
    /** 事件类型,1 关注时自动回复（唯一）,2 消息自动回复(唯一）,3 关键词回复*/
    private String event_type;
    /** 图文ID，对应表weixin_message*/
    private int message_id;
    /** 回复文字内容*/
    private String content;
    /** 创建时间*/
    private Date create_date;

    private Message message;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Message getMessage() { return message; }

    public void setMessage(Message message) { this.message = message; }
}
