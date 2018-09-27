package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeSettlementVO {
    @ApiModelProperty(name = "money",value = "支付金额,单位分",example = "3600")
    private int money;
    @ApiModelProperty(name = "realityMoney",value = "结算金额,单位分",example = "3000")
    private int realityMoney;
    @ApiModelProperty(name = "refund",value = "退款金额,单位分",example = "3600")
    private int refund;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getRealityMoney() {
        return realityMoney;
    }

    public void setRealityMoney(int realityMoney) {
        this.realityMoney = realityMoney;
    }

    public int getRefund() {
        return refund;
    }

    public void setRefund(int refund) {
        this.refund = refund;
    }
}
