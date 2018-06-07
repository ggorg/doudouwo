package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 竞价定金
 */
@ApiModel
public class BiddingEarnestVO {

    @ApiModelProperty(name="price",value="定金金额，单位：分",example="")
    private Integer price;

    @ApiModelProperty(name="code",value="竞价编号，支付时候需要",example="xxxxxxxx")
    private Integer code;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
