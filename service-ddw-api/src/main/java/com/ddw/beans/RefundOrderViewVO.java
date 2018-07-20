package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel
public class RefundOrderViewVO {


    @ApiModelProperty(name="name",value="标题",example="奶茶")
    private String name;

    @ApiModelProperty(name="price",value="价格",example="1000")
    private Integer price;

    @ApiModelProperty(name="refundTime",value="时间",example="yyyy-MM-dd HH:mm:ss")

    private String refundTime;

    @ApiModelProperty(name="orderNo",value="订单号",example="xxxxx")

    private String orderNo;
    @ApiModelProperty(name="imgUrl",value="图片",example="http://xxxxxx")

    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
