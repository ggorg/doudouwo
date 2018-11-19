package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class WithdrawDetailDTO {
    @ApiModelProperty(name="pageNo",value="页码",example="1")

    private Integer pageNo;
    @ApiModelProperty(name="incomeType",value="收益类型，女神收益：1，代练收益：2",example="收益类型，女神收益：1，代练收益：2，不存值是查询所有")
    private Integer incomeType;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }
}
