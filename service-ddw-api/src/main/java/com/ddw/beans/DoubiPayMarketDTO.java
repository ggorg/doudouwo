package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class DoubiPayMarketDTO<DoubiPayMarketItemDTO> {


    @ApiModelProperty(name="items",value="[{code=1,num=2},{code=2,num=2}]",example="[{code=1,num=2},{code=2,num=2}]")
    private List<DoubiPayMarketItemDTO> items;

    public List<DoubiPayMarketItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DoubiPayMarketItemDTO> items) {
        this.items = items;
    }
}
