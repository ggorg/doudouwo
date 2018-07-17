package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeGameApplyVO {
    @ApiModelProperty(name = "orderId",value = "订单编号",example = "1")
    private Integer orderId;
    @ApiModelProperty(name = "payMoney",value = "支付金额单位分,负数则为双倍赔偿金额",example = "3600")
    private Integer payMoney;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }
}
