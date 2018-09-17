package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/14.
 */
@ApiModel
public class PageDTO {
    @ApiModelProperty(name="pageNum",value="页码",example="1")
    private Integer pageNum;
    @ApiModelProperty(name="pageSize",value="显示数量",example="10")
    private Integer pageSize;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
