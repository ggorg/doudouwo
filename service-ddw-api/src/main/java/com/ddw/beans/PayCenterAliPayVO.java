package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class PayCenterAliPayVO implements Serializable {

    @ApiModelProperty(name="orderString",value="订单信息",example="alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018050602643844&biz_content=%7B%22body%22%3....")
    private String orderString;
    @ApiModelProperty(name="orderNo",value="订单号",example="1111111111111111111")
    private String orderNo;

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
