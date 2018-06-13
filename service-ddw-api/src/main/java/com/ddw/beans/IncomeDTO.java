package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 收益明细
 */
@ApiModel(value="收益明细",description="收益明细")
public class IncomeDTO {
    @ApiModelProperty(name="incomeType",value="女神收益：1，代练收益：2",example="1")
    private Integer incomeType;

    @ApiModelProperty(name="pageNo",value="",example="1")
    private Integer pageNo;

    public Integer getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
