package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderViewEvaluateStoreListDTO {


    @ApiModelProperty(name="storeId",value="门店ID",example="门店ID")
    private Integer storeId;
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
