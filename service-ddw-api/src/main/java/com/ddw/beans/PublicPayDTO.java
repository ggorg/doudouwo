package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;

@ApiModel
public class PublicPayDTO extends  PayDTO{
    @ApiModelProperty(name="payType",value="支付类型,微信：1，支付宝：2",example="1")
    private Integer payType;

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}
