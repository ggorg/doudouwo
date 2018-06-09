package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletPracticeInVO {

    @ApiModelProperty(name="practiceIn",value="收益金额，单位分",example="1000")
    @JsonProperty("practiceIn")
    private Integer practiceIncome;

    public Integer getPracticeIncome() {
        return practiceIncome;
    }

    public void setPracticeIncome(Integer practiceIncome) {
        this.practiceIncome = practiceIncome;
    }
}
