package com.ddw.beans;

import com.ddw.enums.PayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class WalletRechargeDTO {
    @ApiModelProperty(name="money",value="金额,单位分",example="1000")
    private Integer money;

    @ApiModelProperty(name="orderType",value="订单类型,充值：3，竞价定金：4，竞价金额：5",example="3")
    private Integer orderType;


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
        return "WalletRechargeDTO{" +
                "money=" + money +
                ", orderType=" + orderType +
                '}';
    }
}
