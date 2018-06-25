package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingOrderInfoVO extends BiddingOrderListVO {

    @ApiModelProperty(name="priceList",value="priceList",example="")
    private List<BiddingPriceListVO> priceList;

    public List<BiddingPriceListVO> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<BiddingPriceListVO> priceList) {
        this.priceList = priceList;
    }
}
