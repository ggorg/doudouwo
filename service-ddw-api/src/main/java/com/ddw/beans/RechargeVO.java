package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class RechargeVO {

    @ApiModelProperty(name="id",value="编号",example="1")
    private Integer id;

    @ApiModelProperty(name="name",value="名称",example="100元")
    private String name;

    @ApiModelProperty(name="desc",value="描述",example="xxxx")
    private String desc;

    @ApiModelProperty(name="price",value="价格",example="xxxx")
    private Integer price;
    @ApiModelProperty(name="discount",value="优惠价格",example="xxxx")
    private Integer discount;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
