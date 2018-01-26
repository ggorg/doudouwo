package com.weixin.entity;

import java.util.Date;

/**
 * 微信自定义菜单
 * Created by Jacky on 2017/12/5.
 */
public class Menu{
    private int id;
    /** 公众号ID*/
    private String appid;
    /** 菜单标题，不超过16个字节，子菜单不超过60个字节*/
    private String name;
    /** 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型*/
    private String type;
    /** 菜单KEY值，用于消息接口推送，不超过128字节*/
    private String key;
    /** 网页链接，用户点击菜单可打开链接，不超过1024字节。type为miniprogram时，不支持小程序的老版本客户端将打开本url*/
    private String url;
    /** 父级编号*/
    private int parent_id;
    /** 排序*/
    private int sort;

    /** 创建时间*/
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
