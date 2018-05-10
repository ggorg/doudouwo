package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/4/29.
 */
public class ReviewRealNamePO {
    private Integer id;
    private String drBusinessCode;
    private Integer userId;
    private String realName;
    private String idcard;
    private String idcardFrontUrl;
    private String idcardOppositeUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcardFrontUrl() {
        return idcardFrontUrl;
    }

    public void setIdcardFrontUrl(String idcardFrontUrl) {
        this.idcardFrontUrl = idcardFrontUrl;
    }

    public String getIdcardOppositeUrl() {
        return idcardOppositeUrl;
    }

    public void setIdcardOppositeUrl(String idcardOppositeUrl) {
        this.idcardOppositeUrl = idcardOppositeUrl;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDrBusinessCode() {
        return drBusinessCode;
    }

    public void setDrBusinessCode(String drBusinessCode) {
        this.drBusinessCode = drBusinessCode;
    }
}
