package com.ddw.beans;

import java.util.Date;

public class TicketPO {
    private Integer id;
    private String dtName;
    private Integer dtPrice;
    private Integer dtActPrice;
    private Integer dtType;
    private String dtActiveTime;
    private String dtImgPath;

    private Date updateTime;
    private Date createTime;
    private String dtDesc;
    private Integer dtDisabled;

    public String getDtImgPath() {
        return dtImgPath;
    }

    public void setDtImgPath(String dtImgPath) {
        this.dtImgPath = dtImgPath;
    }

    public Integer getDtDisabled() {
        return dtDisabled;
    }

    public void setDtDisabled(Integer dtDisabled) {
        this.dtDisabled = dtDisabled;
    }

    public String getDtDesc() {
        return dtDesc;
    }

    public void setDtDesc(String dtDesc) {
        this.dtDesc = dtDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDtName() {
        return dtName;
    }

    public void setDtName(String dtName) {
        this.dtName = dtName;
    }

    public Integer getDtPrice() {
        return dtPrice;
    }

    public void setDtPrice(Integer dtPrice) {
        this.dtPrice = dtPrice;
    }

    public Integer getDtActPrice() {
        return dtActPrice;
    }

    public void setDtActPrice(Integer dtActPrice) {
        this.dtActPrice = dtActPrice;
    }

    public Integer getDtType() {
        return dtType;
    }

    public void setDtType(Integer dtType) {
        this.dtType = dtType;
    }

    public String getDtActiveTime() {
        return dtActiveTime;
    }

    public void setDtActiveTime(String dtActiveTime) {
        this.dtActiveTime = dtActiveTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
