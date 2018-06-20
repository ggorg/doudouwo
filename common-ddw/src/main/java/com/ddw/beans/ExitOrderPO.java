package com.ddw.beans;

import java.io.Serializable;
import java.util.Date;


public class ExitOrderPO implements Serializable {
    private static final long serialVersionUID = -2754876779624263760L;
    private Integer id;
    private Date createTime;
    private Integer orderId;
    private String orderNo;
    private String exitOrderNo;
    private Integer orderType;
    private Integer creater;
    private Integer totalCost;
    private Integer exitCost;
    private String createrName;

    @Override
    public String toString() {
        return "ExitOrderPO{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", exitOrderNo='" + exitOrderNo + '\'' +
                ", orderType=" + orderType +
                ", creater=" + creater +
                ", totalCost=" + totalCost +
                ", exitCost=" + exitCost +
                ", createrName='" + createrName + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getExitOrderNo() {
        return exitOrderNo;
    }

    public void setExitOrderNo(String exitOrderNo) {
        this.exitOrderNo = exitOrderNo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getCreater() {
        return creater;
    }

    public void setCreater(Integer creater) {
        this.creater = creater;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getExitCost() {
        return exitCost;
    }

    public void setExitCost(Integer exitCost) {
        this.exitCost = exitCost;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }
}
