package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class BiddingDTO implements Serializable {



    @ApiModelProperty(name="price",value="价格，单位：分",example="xxx")
    private Integer price;

    @ApiModelProperty(name="time",value="时间,单位：分钟",example="1")
    private Integer time;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }


}
