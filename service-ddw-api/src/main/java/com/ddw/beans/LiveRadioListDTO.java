package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

public class LiveRadioListDTO {
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    @ApiModelProperty(name="langlat",value="坐标",example="116.493956,39.960963")
    private String langlat;

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
}
