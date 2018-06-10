package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderViewVO {


    @ApiModelProperty(name="name",value="标题",example="奶茶")
    private String name;
    @ApiModelProperty(name="headImg",value="图像地址,除了商品和礼物，其它都是空",example="http://")
    private String headImg;
    @ApiModelProperty(name="price",value="价格",example="1000")
    private String price;

    @ApiModelProperty(name="createTime",value="时间",example="yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiModelProperty(name="orderNo",value="订单号",example="xxxxx")

    private String orderNo;
    @ApiModelProperty(name="num",value="数量",example="1")
    private Integer num;
    @ApiModelProperty(name="orderType",value="订单类型",example="1")
    private Integer orderType;
    @ApiModelProperty(name="shipStatus",value="发货状态,未发货：0，已接单：1，完成：5",example="1")
    private Integer shipStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(Integer shipStatus) {
        this.shipStatus = shipStatus;
    }
}
