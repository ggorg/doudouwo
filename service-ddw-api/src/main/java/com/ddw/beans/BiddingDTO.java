package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class BiddingDTO implements Serializable {



    @ApiModelProperty(name="price",value="价格",example="xxx，分单位")
    private Integer price;


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }


}
