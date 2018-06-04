package com.ddw.beans;

import java.util.Date;

public class GiftPO {
    private Integer id;
    private String dgName;
    private Integer dgPrice;
    private Integer dgActPrice;
    private String dgImgPath;
    private Integer dgSort;
    private Date updateTime;
    private Date createTime;
    private Integer dgDisabled;

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

    public Integer getDgDisabled() {
        return dgDisabled;
    }

    public void setDgDisabled(Integer dgDisabled) {
        this.dgDisabled = dgDisabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgName() {
        return dgName;
    }

    public void setDgName(String dgName) {
        this.dgName = dgName;
    }

    public Integer getDgPrice() {
        return dgPrice;
    }

    public void setDgPrice(Integer dgPrice) {
        this.dgPrice = dgPrice;
    }

    public Integer getDgActPrice() {
        return dgActPrice;
    }

    public void setDgActPrice(Integer dgActPrice) {
        this.dgActPrice = dgActPrice;
    }

    public String getDgImgPath() {
        return dgImgPath;
    }

    public void setDgImgPath(String dgImgPath) {
        this.dgImgPath = dgImgPath;
    }

    public Integer getDgSort() {
        return dgSort;
    }

    public void setDgSort(Integer dgSort) {
        this.dgSort = dgSort;
    }
}
