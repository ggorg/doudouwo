package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
public class WalletTransferMoneyDTO {
    @ApiModelProperty(name="money",value="金额",example="111111")
    private Integer money;
    @ApiModelProperty(name="incomeType",value="收益类型，女神收益：1，代练收益：2",example="1")
    private Integer incomeType;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }
}
