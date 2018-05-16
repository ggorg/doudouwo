package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PublicPayDTO {

    @ApiModelProperty(name="money",value="金额，单位分",example="1000")
    private Integer money;


    @ApiModelProperty(name="payType",value="支付类型,微信：1，支付宝：2",example="1")
    private Integer payType;

    @ApiModelProperty(name="orderType",value="订单类型,充值：3，竞价定金：4",example="3")
    private Integer orderType;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "PublicPayDTO{" +
                "money=" + money +
                ", payType=" + payType +
                ", orderType=" + orderType +
                '}';
    }
}
