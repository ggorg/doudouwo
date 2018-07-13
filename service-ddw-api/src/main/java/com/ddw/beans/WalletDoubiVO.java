package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletDoubiVO {

    @ApiModelProperty(name="coin",value="逗币",example="1000")
    private Integer coin;

    @ApiModelProperty(name="expenseCoin",value="贡献值（已花费的逗币）",example="1000")
    private Integer expenseCoin;

    public Integer getExpenseCoin() {
        return expenseCoin;
    }

    public void setExpenseCoin(Integer expenseCoin) {
        this.expenseCoin = expenseCoin;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }
}
