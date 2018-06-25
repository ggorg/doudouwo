package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class BiddingPriceListVO implements Serializable {


    private static final long serialVersionUID = -58762849106500400L;
    @ApiModelProperty(name="orderType",value="订单类型",example="订单类型")
    private Integer orderType;
    @ApiModelProperty(name="orderTypeName",value="订单类型名称",example="订单类型名称")
    private String orderTypeName;

    @ApiModelProperty(name="orderNo",value="订单号",example="订单号")
    private String orderNo;
    @ApiModelProperty(name="createTime",value="创建时间",example="创建时间")
    private String createTime;
    @ApiModelProperty(name="price",value="金额",example="金额")
    private Integer price;

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
