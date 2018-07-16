package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeSettlementVO {
    @ApiModelProperty(name = "payMoney",value = "支付金额单位分,负数则为双倍赔偿金额",example = "3600")
    private int payMoney;

    public int getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(int payMoney) {
        this.payMoney = payMoney;
    }
}
