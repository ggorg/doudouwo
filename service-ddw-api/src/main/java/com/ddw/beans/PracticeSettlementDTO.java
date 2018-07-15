package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeSettlementDTO {
    @ApiModelProperty(name = "orderId",value = "订单编号",example = "1")
    private int orderId;
    @ApiModelProperty(name = "realityRankId",value = "当前段位编号",example = "2")
    private int realityRankId;
    @ApiModelProperty(name = "realityStar",value = "当前星",example = "1")
    private int realityStar;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getRealityRankId() {
        return realityRankId;
    }

    public void setRealityRankId(int realityRankId) {
        this.realityRankId = realityRankId;
    }

    public int getRealityStar() {
        return realityStar;
    }

    public void setRealityStar(int realityStar) {
        this.realityStar = realityStar;
    }
}
