package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class PracticeEvaluationDetailDTO {
    @ApiModelProperty(name = "orderId",value = "订单编号",example = "1")
    private int orderId;
    @ApiModelProperty(name = "star",value = "星,最低两颗星",example = "2")
    private int star;
    @ApiModelProperty(name = "describe",value = "评价",example = "这个代练好垃圾,掉星坑货")
    private String describe;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
