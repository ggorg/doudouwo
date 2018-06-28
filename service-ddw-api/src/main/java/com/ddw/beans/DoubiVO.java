package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DoubiVO extends RechargeVO {
    @ApiModelProperty(name="doubiNum",value="逗币数额",example="1")
    private Integer doubiNum;

    public Integer getDoubiNum() {
        return doubiNum;
    }

    public void setDoubiNum(Integer doubiNum) {
        this.doubiNum = doubiNum;
    }
}
