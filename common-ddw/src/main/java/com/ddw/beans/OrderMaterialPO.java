package com.ddw.beans;

import java.io.Serializable;
import java.util.Date;

public class OrderMaterialPO implements Serializable {
    private Integer id;
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 原材料ID
     */
    private Integer materialId;
    /**
     * 原材料名称
     */
    private String materialName;
    /**
     * 原材料总额
     */
    private Integer materialCountPrice;

    /**
     * 原材料单价
     */
    private Integer materialUnitPrice;

    /**
     * 原材料购买数量
     */
    private Integer materialBuyNumber;
    private Date createTime;
    private Date updateTime;
    private Integer materialCountNetWeight;

    private String materialUnit;

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public Integer getMaterialCountNetWeight() {
        return materialCountNetWeight;
    }

    public void setMaterialCountNetWeight(Integer materialCountNetWeight) {
        this.materialCountNetWeight = materialCountNetWeight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getMaterialCountPrice() {
        return materialCountPrice;
    }

    public void setMaterialCountPrice(Integer materialCountPrice) {
        this.materialCountPrice = materialCountPrice;
    }

    public Integer getMaterialUnitPrice() {
        return materialUnitPrice;
    }

    public void setMaterialUnitPrice(Integer materialUnitPrice) {
        this.materialUnitPrice = materialUnitPrice;
    }

    public Integer getMaterialBuyNumber() {
        return materialBuyNumber;
    }

    public void setMaterialBuyNumber(Integer materialBuyNumber) {
        this.materialBuyNumber = materialBuyNumber;
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
