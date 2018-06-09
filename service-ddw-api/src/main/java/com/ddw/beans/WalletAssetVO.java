package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletAssetVO {

    @ApiModelProperty(name="money",value="余额，单位分",example="1000")
    private Integer money;

    @ApiModelProperty(name="goddessIn",value="女神收益，单位分",example="1000")
    @JsonProperty("goddessIn")
    private Integer goddessIncome;


    @ApiModelProperty(name="practiceIn",value="代练收益，单位分",example="1000")
    @JsonProperty("practiceIn")
    private Integer practiceIncome;

    public Integer getGoddessIncome() {
        return goddessIncome;
    }

    public void setGoddessIncome(Integer goddessIncome) {
        this.goddessIncome = goddessIncome;
    }

    public Integer getPracticeIncome() {
        return practiceIncome;
    }

    public void setPracticeIncome(Integer practiceIncome) {
        this.practiceIncome = practiceIncome;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
