package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class GoodsInfoProductVO<T> {
    @ApiModelProperty(name="code",value="标识号",example="1")
    @JsonProperty("code")
    private Integer id;

    @ApiModelProperty(name="name",value="规格名称",example="奶茶")
    @JsonProperty("name")
    private String dghName;

    @ApiModelProperty(name="price",value="价格，单位分",example="100")
    @JsonProperty("price")
    private Integer dghSalesPrice;

    @ApiModelProperty(name="actPrice",value="活动价格",example="")
    @JsonProperty("actPrice")
    private Integer dghActivityPrice;

    @ApiModelProperty(name="salesNum",value="销量",example="0")
    private Integer dghSaleNumber;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDghName() {
        return dghName;
    }

    public void setDghName(String dghName) {
        this.dghName = dghName;
    }

    public Integer getDghSalesPrice() {
        return dghSalesPrice;
    }

    public void setDghSalesPrice(Integer dghSalesPrice) {
        this.dghSalesPrice = dghSalesPrice;
    }

    public Integer getDghActivityPrice() {
        return dghActivityPrice;
    }

    public void setDghActivityPrice(Integer dghActivityPrice) {
        this.dghActivityPrice = dghActivityPrice;
    }

    public Integer getDghSaleNumber() {
        return dghSaleNumber;
    }

    public void setDghSaleNumber(Integer dghSaleNumber) {
        this.dghSaleNumber = dghSaleNumber;
    }
}
