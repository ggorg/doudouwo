package com.ddw.beans;

import java.util.Date;

public class OrderPO {
    private Integer id;
    private Date createTime;
    private Date updateTime;

    /**
     * 所属用户
     */
    private Integer doUserId;

    /**
     * 付款状态，未付款：0，已付款：1，退款：2
     */
    private Integer doPayStatus;

    /**
     * 订单类型，商品：1，原材料：2
     */
    private Integer doType;

    /**
     * 订单截止时间
     */
    private Date doEndTime;

    /**
     * 发货状态，未发货：0，已发货：1，确认签收：2，退货：3，完成：4
     */
    private Integer doShipStatus;

    /**
     * 订单日期
     */
    private String doOrderDate;

    /**
     * 支付类型，微信支付：1，支付宝：2，线下支付：3
     */
    private Integer doPayType;

    /**
     * 门店ID，此处是材料用户标识的，app订单统一是-1，其余是门店ID
     */
    private Integer doStoreId;

    /**
     * 卖家角色，总部：-1，门店：id号
     */
    private Integer doSellerId;

    /**
     * 优惠卷
     */
    private String doCouponNo;

    /**
     * 用户类型 ,普通会员：0，女神会员：1，代练会员：2，服务员：3，门店：1
     */
    private Integer doUserType;

    @Override
    public String toString() {
        return "OrderPO{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", doUserId=" + doUserId +
                ", doPayStatus=" + doPayStatus +
                ", doType=" + doType +
                ", doEndTime=" + doEndTime +
                ", doShipStatus=" + doShipStatus +
                ", doOrderDate='" + doOrderDate + '\'' +
                ", doPayType=" + doPayType +
                ", doStoreId=" + doStoreId +
                ", doSellerId=" + doSellerId +
                ", doCouponNo='" + doCouponNo + '\'' +
                ", doUserType=" + doUserType +
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDoUserId() {
        return doUserId;
    }

    public void setDoUserId(Integer doUserId) {
        this.doUserId = doUserId;
    }

    public Integer getDoPayStatus() {
        return doPayStatus;
    }

    public void setDoPayStatus(Integer doPayStatus) {
        this.doPayStatus = doPayStatus;
    }

    public Integer getDoType() {
        return doType;
    }

    public void setDoType(Integer doType) {
        this.doType = doType;
    }

    public Date getDoEndTime() {
        return doEndTime;
    }

    public void setDoEndTime(Date doEndTime) {
        this.doEndTime = doEndTime;
    }

    public Integer getDoShipStatus() {
        return doShipStatus;
    }

    public void setDoShipStatus(Integer doShipStatus) {
        this.doShipStatus = doShipStatus;
    }

    public String getDoOrderDate() {
        return doOrderDate;
    }

    public void setDoOrderDate(String doOrderDate) {
        this.doOrderDate = doOrderDate;
    }

    public Integer getDoPayType() {
        return doPayType;
    }

    public void setDoPayType(Integer doPayType) {
        this.doPayType = doPayType;
    }

    public Integer getDoStoreId() {
        return doStoreId;
    }

    public void setDoStoreId(Integer doStoreId) {
        this.doStoreId = doStoreId;
    }

    public Integer getDoSellerId() {
        return doSellerId;
    }

    public void setDoSellerId(Integer doSellerId) {
        this.doSellerId = doSellerId;
    }

    public String getDoCouponNo() {
        return doCouponNo;
    }

    public void setDoCouponNo(String doCouponNo) {
        this.doCouponNo = doCouponNo;
    }

    public Integer getDoUserType() {
        return doUserType;
    }

    public void setDoUserType(Integer doUserType) {
        this.doUserType = doUserType;
    }
}
