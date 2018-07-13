package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class WithdrawDTO {
    @ApiModelProperty(name="code",value="提现途径的id号",example="1")
    private Integer code;

    @ApiModelProperty(name="money",value="提现金额,单位：分",example="1000")
    private Integer money;
    @ApiModelProperty(name="incomeType",value="收益类型，女神：1，代练：2",example="1")
    private Integer incomeType;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

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
