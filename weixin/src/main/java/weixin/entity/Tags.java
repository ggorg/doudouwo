package weixin.entity;

import java.util.Date;

/**
 * 用户标签管理实体bean
 * Created by Jacky on 2017/12/26.
 */
public class Tags {
    private String appid;
    private int id;
    /** 标签名（30个字符以内）*/
    private String name;
    /** 粉丝数量*/
    private int count;
    /**创建时间 */
    private Date create_date;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
