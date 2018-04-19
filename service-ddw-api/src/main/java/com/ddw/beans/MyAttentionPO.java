package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/4/19.
 */
public class MyAttentionPO {
    private int id;
    private int userId;
    private int goddessId;
    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(int goddessId) {
        this.goddessId = goddessId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
