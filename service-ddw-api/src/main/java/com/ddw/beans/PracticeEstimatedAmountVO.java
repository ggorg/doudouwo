package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeEstimatedAmountVO {
    @ApiModelProperty(name = "payMoney",value = "支付金额单位分",example = "3600")
    private Integer payMoney;

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }
}
