package com.weixin.entity;

import java.util.Date;

/**
 * Created by Jacky on 2018/11/23.
 */
public class OldBringingNew {
    private Integer id;
    private String oldOpenid;
    private String newOpenid;
    private String newNickName;
    private String newHeadImgUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOldOpenid() {
        return oldOpenid;
    }

    public void setOldOpenid(String oldOpenid) {
        this.oldOpenid = oldOpenid;
    }

    public String getNewOpenid() {
        return newOpenid;
    }

    public void setNewOpenid(String newOpenid) {
        this.newOpenid = newOpenid;
    }

    public String getNewNickName() {
        return newNickName;
    }

    public void setNewNickName(String newNickName) {
        this.newNickName = newNickName;
    }

    public String getNewHeadImgUrl() {
        return newHeadImgUrl;
    }

    public void setNewHeadImgUrl(String newHeadImgUrl) {
        this.newHeadImgUrl = newHeadImgUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
