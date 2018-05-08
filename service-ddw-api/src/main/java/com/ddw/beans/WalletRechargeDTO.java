package com.ddw.beans;

import com.ddw.enums.PayTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class WalletRechargeDTO {
    @ApiModelProperty(name="money",value="充值金额",example="1000，单位分")
    private Integer money;


    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

}
