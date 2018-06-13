package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel
public class IncomeVO {

    @ApiModelProperty(name="money",value="金额，单位分",example="1")
    private Integer money;

    @ApiModelProperty(name="type",value="类型",example="女神：1，代练：2")
    private Integer type;

    @ApiModelProperty(name="createTime",value="时间",example="MM/dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "MM/dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(name="orderNo",value="订单",example="xxxx")
    private String orderNo;

    @ApiModelProperty(name="orderType",value="订单类型",example="竞价定金：4，竞价金额：5，礼物：6")
    private Integer orderType;

    @ApiModelProperty(name="orderTypeName",value="订单类型称",example="")
    private String orderTypeName;

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
