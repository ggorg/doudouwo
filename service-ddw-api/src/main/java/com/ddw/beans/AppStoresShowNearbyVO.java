package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 展示附近门店响应
 */
@ApiModel
public class AppStoresShowNearbyVO {
    @ApiModelProperty(name="name",value="门店名称",example="xxx门店")
    private String dsName;
    @ApiModelProperty(name="distance",value="距离，单位：米",example="1001")
    private String distance;

    @ApiModelProperty(name="storeId",value="门店Id",example="1")
    private Integer id;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @ApiModelProperty(name="imgUrl",value="门店照片",example="http://xxxxxxx")
    private String imgUrl

            ;

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
