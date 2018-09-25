package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class GiftUseDTO {

    @ApiModelProperty(name="code",value="礼物code",example="礼物code")
    private Integer code;

    @ApiModelProperty(name="num",value="数量",example="11")
    private Integer num;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
