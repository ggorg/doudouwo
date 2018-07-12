package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class BiddingSearchWaitPayDTO {

    @ApiModelProperty(name="bidCode",value="竞价ID，在直播间外就传此参数",example="")
    private Integer bidCode;

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
    }

    @Override
    public String toString() {
        return "BiddingSearchWaitPayDTO{" +
                "bidCode=" + bidCode +
                '}';
    }
}
