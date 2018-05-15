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

    @ApiModelProperty(name="bidCode",value="竞价标识号",example="xxxxxxxx")
    private String bidCode;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBidCode() {
        return bidCode;
    }

    public void setBidCode(String bidCode) {
        this.bidCode = bidCode;
    }
}
