package com.ddw.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * 女神
 */
public class GoddessPO implements Serializable {
    private int id;
    private int userId;
    private String tableNo;
    private Date createTime;
    private Date updateTime;
    private Integer appointment;
    private Integer bidPrice;
    private Integer earnest;
    private Integer storeId;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getAppointment() {
        return appointment;
    }

    public void setAppointment(Integer appointment) {
        this.appointment = appointment;
    }

    public Integer getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Integer bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Integer getEarnest() {
        return earnest;
    }

    public void setEarnest(Integer earnest) {
        this.earnest = earnest;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
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
