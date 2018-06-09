package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletGoddessInVO {

    @ApiModelProperty(name="goddessIn",value="收益金额，单位分",example="1000")
    @JsonProperty("goddessIn")
    private Integer goddessIncome;

    public Integer getGoddessIncome() {
        return goddessIncome;
    }

    public void setGoddessIncome(Integer goddessIncome) {
        this.goddessIncome = goddessIncome;
    }
}
