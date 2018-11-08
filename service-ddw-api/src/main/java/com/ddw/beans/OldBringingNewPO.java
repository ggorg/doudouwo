package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/10/29.
 */
public class OldBringingNewPO {
    private Integer id;
    private String oldOpenid;
    private String newOpenid;
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
