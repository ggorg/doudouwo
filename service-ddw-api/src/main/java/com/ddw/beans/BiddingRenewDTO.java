package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class BiddingRenewDTO implements Serializable {


    private static final long serialVersionUID = 6459340461787673454L;
   // @ApiModelProperty(name="price",value="价格，单位：分",example="xxx")
   // private Integer price;

    @ApiModelProperty(name="time",value="时间,单位：分钟",example="1")
    private Integer time;

    @ApiModelProperty(name="bidCode",value="竞价code",example="xxx")
    private Integer bidCode;

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "BiddingRenewDTO{" +
                "time=" + time +
                ", bidCode=" + bidCode +
                '}';
    }
}
