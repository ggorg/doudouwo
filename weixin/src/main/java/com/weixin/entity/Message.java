package com.weixin.entity;

import java.util.Date;

/**
 * 图文消息实体bean
 * Created by Jacky on 2017/12/10.
 */
public class Message {
    /*主键id*/
    private int id;
    /*应用id*/
    private String appid;
    /*图文消息的标题*/
    private String title;
    /*图文消息的描述*/
    private String description;
    /*图文消息被点击后跳转的链接*/
    private String url;
    /*图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80*/
    private String picurl;
    /*父级编号*/
    private int parent_id;
    /**/
    private int sort;
    private Date create_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }
}
