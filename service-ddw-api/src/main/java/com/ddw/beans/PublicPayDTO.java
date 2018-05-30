package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;

@ApiModel
public class PublicPayDTO {

    @ApiModelProperty(name="money",value="金额,单位分,竞价定金和竞价金额必须传金额，其它不用",example="1000")
    private Integer money;


    @ApiModelProperty(name="payType",value="支付类型,微信：1，支付宝：2",example="1")
    private Integer payType;

    @ApiModelProperty(name="orderType",value="订单类型,商品：1，充值：3，竞价定金：4，竞价金额：5",example="3")
    private Integer orderType;

    @ApiModelProperty(name="codes",value="所购买的多个物品编号（充值卷的ID，商品ID，礼物ID等）,竞价定金和竞价金额不用传，其它必须传",example="")
    private Integer codes[];

    public Integer[] getCodes() {
        return codes;
    }

    public void setCodes(Integer[] codes) {
        this.codes = codes;
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

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "PublicPayDTO{" +
                "money=" + money +
                ", payType=" + payType +
                ", orderType=" + orderType +
                ", codes=" + Arrays.toString(codes) +
                '}';
    }
}
