package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletDealCountDTO {
    @ApiModelProperty(name="date",value="日期",example="2018-09")
    private String date;

    @ApiModelProperty(name="type",value="统计类型，支出：1，收入：2",example="统计类型，支出：1，收入：2")
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
