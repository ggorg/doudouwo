package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/6/14.
 */
public class BannerPO {
    private Integer id;
    private String drBusinessCode;
    private String name;
    private Integer storeId;
    private String picUrl;
    private String url;
    private Integer status;
    private String bDescribe;
    private Date createTime;
    private Integer bType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrBusinessCode() {
        return drBusinessCode;
    }

    public void setDrBusinessCode(String drBusinessCode) {
        this.drBusinessCode = drBusinessCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getbDescribe() {
        return bDescribe;
    }

    public void setbDescribe(String bDescribe) {
        this.bDescribe = bDescribe;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getbType() {
        return bType;
    }

    public void setbType(Integer bType) {
        this.bType = bType;
    }
}
