package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletDealCountVO<T> {

    @ApiModelProperty(name="costCount",value="查询月份的统计",example="查询月份的统计")
    private Integer costCount=0;

    @ApiModelProperty(name="dealCount",value="交易笔数",example="交易笔数")
    private Integer dealCount=0;

    @ApiModelProperty(name="list",value="各个月的统计",example="各个月的统计")
    private List list;

    public Integer getDealCount() {
        return dealCount;
    }

    public void setDealCount(Integer dealCount) {
        this.dealCount = dealCount;
    }

    public Integer getCostCount() {
        return costCount;
    }

    public void setCostCount(Integer costCount) {
        this.costCount = costCount;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
