package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/9/19.
 */
public class GoddessQueryListDTO {
    @ApiModelProperty(name = "weekList",value = "查询周榜传1,总榜不传或非1",example = "1")
    private Integer weekList;
    private PageDTO page;

    public Integer getWeekList() {
        return weekList;
    }

    public void setWeekList(Integer weekList) {
        this.weekList = weekList;
    }

    public PageDTO getPage() {
        return page;
    }

    public void setPage(PageDTO page) {
        this.page = page;
    }
}
