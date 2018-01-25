package weixin.entity;

import java.util.Date;

/**
 * 推送策略实体bean
 * Created by Jacky on 2017/12/18.
 */
public class Push {
    /** 主键ID*/
    private int id;
    /** 公众号ID*/
    private String appid;
    /** 标题*/
    private String title;
    /** 推送类型,1 文字,2 图文,3 模板消息*/
    private int push_type;
    /** 推送内容*/
    private String push_content;
    /** 推送图文id*/
    private int push_messageid;
    /** 推送模板id*/
    private int push_templateid;
    /** 微信用户标签id，对应表weixin_tag*/
    private int tag_id;
    /** 推送时间*/
    private Date push_time;
    /** 创建时间*/
    private Date create_date;
    /** 推送状态,0 未推送,1 已推送*/
    private int push_state;

    private Message message;

    private Tags tags;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getPush_content() {
        return push_content;
    }

    public void setPush_content(String push_content) {
        this.push_content = push_content;
    }

    public int getPush_messageid() {
        return push_messageid;
    }

    public void setPush_messageid(int push_messageid) {
        this.push_messageid = push_messageid;
    }

    public int getPush_templateid() {
        return push_templateid;
    }

    public void setPush_templateid(int push_templateid) {
        this.push_templateid = push_templateid;
    }

    public Date getPush_time() {
        return push_time;
    }

    public void setPush_time(Date push_time) {
        this.push_time = push_time;
    }

    public int getPush_type() {
        return push_type;
    }

    public void setPush_type(int push_type) {
        this.push_type = push_type;
    }

    public int getPush_state() {
        return push_state;
    }

    public void setPush_state(int push_state) {
        this.push_state = push_state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }
}
