package weixin.entity;

import java.util.Date;

/**
 * 微信事件处理-关键字回复
 * Created by Jacky on 2017/12/15.
 */
public class EventKey {

    /** 主键ID*/
    private int id;
    /** 公众号ID*/
    private String appid;
    /** 关键词*/
    private String key;
    /** 回复规则id，对应weixin_event_rule表*/
    private int rule_id;
    /** 匹配规则，1 完全匹配，0 模糊匹配*/
    private int match;
    /** 创建时间*/
    private Date create_date;

    private EventRule eventRule;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public int getRule_id() {
        return rule_id;
    }

    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }

    public EventRule getEventRule() { return eventRule; }

    public void setEventRule(EventRule eventRule) { this.eventRule = eventRule; }
}
