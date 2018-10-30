package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DoubiPayMarketItemDTO {


    @ApiModelProperty(name="code",value="礼物编号",example="礼物编号")
    private Integer code;

    @JsonIgnore
    private Integer[] codes;

    @ApiModelProperty(name="num",value="数量",example="11")
    private Integer num;


    public Integer[] getCodes() {
        return codes;
    }

    public void setCodes(Integer[] codes) {
        this.codes = codes;
    }

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
