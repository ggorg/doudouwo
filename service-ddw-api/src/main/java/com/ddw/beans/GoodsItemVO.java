package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel
public class GoodsItemVO {

    @ApiModelProperty(name="code",value="商品标识号",example="1")
    @JsonProperty("code")
    private Integer id;

    @ApiModelProperty(name="name",value="商品名称",example="奶茶")
    @JsonProperty("name")
    private String dgTitle;

    @ApiModelProperty(name="imgUrl",value="商品图片",example="http://xxxxxxxxxx")
    @JsonProperty("imgUrl")
    private String fileImgIcoPath;

    @ApiModelProperty(name="desc",value="简介",example="xxxxx")
    @JsonProperty("desc")
    private String dgDetail;

    @ApiModelProperty(name="monthSales",value="月销量",example="10")
    private Integer monthSales;
    @ApiModelProperty(name="salesCountNum",value="总销量",example="10")
    @JsonProperty("salesCountNum")
    private Integer dgSalesNumber=0;

    @ApiModelProperty(name="likeNum",value="点赞",example="10")
    private Integer likeNum;

    private List products;

    public Integer getDgSalesNumber() {
        return dgSalesNumber;
    }

    public void setDgSalesNumber(Integer dgSalesNumber) {
        this.dgSalesNumber = dgSalesNumber;
    }

    public List getProducts() {
        return products;
    }

    public void setProducts(List products) {
        this.products = products;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgTitle() {
        return dgTitle;
    }

    public void setDgTitle(String dgTitle) {
        this.dgTitle = dgTitle;
    }

    public String getFileImgIcoPath() {
        return fileImgIcoPath;
    }

    public void setFileImgIcoPath(String fileImgIcoPath) {
        this.fileImgIcoPath = fileImgIcoPath;
    }

    public String getDgDetail() {
        return dgDetail;
    }

    public void setDgDetail(String dgDetail) {
        this.dgDetail = dgDetail;
    }

    public Integer getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(Integer monthSales) {
        this.monthSales = monthSales;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }
}
