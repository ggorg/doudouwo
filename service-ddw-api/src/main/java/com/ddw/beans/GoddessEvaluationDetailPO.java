package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/7/7.
 */
public class GoddessEvaluationDetailPO {
    private Integer id;
    private Integer userId;
    private Integer goddessId;
    private Integer star;
    private String describe;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(Integer goddessId) {
        this.goddessId = goddessId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
