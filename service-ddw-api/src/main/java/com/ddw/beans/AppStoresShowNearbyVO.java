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
