package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletDealRecordDTO {
    @ApiModelProperty(name="date",value="日期",example="2018-09")
    private String date;
    @ApiModelProperty(name="pageNo",value="页码",example="页码")
    private Integer pageNo;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
