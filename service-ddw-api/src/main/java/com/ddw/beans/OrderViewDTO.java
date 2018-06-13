package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
public class OrderViewDTO {

    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    @ApiModelProperty(name="type",value="类型,女神：1，代练：2，所有：3，",example="1")
    private Integer type;


    @ApiModelProperty(name="shipStatus",value="未接单（未发货）：0，已接单：1，已发货：2，已完成：5",example="1")
    private Integer shipStatus;

    public Integer getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(Integer shipStatus) {
        this.shipStatus = shipStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
