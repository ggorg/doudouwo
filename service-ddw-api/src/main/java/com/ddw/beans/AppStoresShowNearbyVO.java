package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 展示附近门店响应
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppStoresShowNearbyVO {

    @ApiModelProperty(name="name",value="门店名称",example="xxx门店")
    @JsonProperty("name")
    private String dsName;
    @ApiModelProperty(name="distance",value="距离，单位：千米",example="1001.1km")
    private String distance;
    @ApiModelProperty(name="address",value="地址",example="地址")
    @JsonProperty("address")
    private String dsAddress;
    @ApiModelProperty(name="storeId",value="门店Id",example="1")
    private Integer id;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @ApiModelProperty(name="imgUrl",value="门店照片",example="http://xxxxxxx")
    private String imgUrl;

    public String getDsAddress() {
        return dsAddress;
    }

    public void setDsAddress(String dsAddress) {
        this.dsAddress = dsAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
