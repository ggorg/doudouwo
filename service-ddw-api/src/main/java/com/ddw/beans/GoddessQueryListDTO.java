package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/9/19.
 */
public class GoddessQueryListDTO {
    @ApiModelProperty(name = "weekList",value = "查询周榜传1,总榜不传或非1",example = "1")
    private Integer weekList;
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    public Integer getWeekList() {
        return weekList;
    }

    public void setWeekList(Integer weekList) {
        this.weekList = weekList;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
