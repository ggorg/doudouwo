package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * 展示附近门店请求
 */
@ApiModel
public class AppStoresShowNearbyDTO {

    @ApiModelProperty(name="langlat",value="坐标",example="116.493956,39.960963")
    private String langlat;

    @ApiModelProperty(name="city",value="地区",example="成都")
    @NotNull(message = "地区是空")
    private String city;

    @ApiModelProperty(name="pageNo",value="页数，默认从1开始",example="1")
    @ApiParam(defaultValue="1")
    private Integer pageNo;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getLanglat() {
        return langlat;
    }

    public void setLanglat(String langlat) {
        this.langlat = langlat;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
