package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageVO<T> {


    @ApiModelProperty(name="total",value="总记录数",example="100")
    private String total;

    @ApiModelProperty(name="pages",value="总页数",example="10")
    private String pages;

    private List<T> list;

    public PageVO() {
    }

    public PageVO(String total, String pages, List<T> list) {
        this.total = total;
        this.pages = pages;
        this.list = list;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
