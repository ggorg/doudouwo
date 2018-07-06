package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class BiddingCodeDTO implements Serializable {


    private static final long serialVersionUID = 4127993572607881222L;
    @ApiModelProperty(name="bidCode",value="竞价code",example="xxx")
    private Integer bidCode;

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
    }

    @Override
    public String toString() {
        return "BiddingCodeDTO{" +
                "bidCode=" + bidCode +
                '}';
    }
}
