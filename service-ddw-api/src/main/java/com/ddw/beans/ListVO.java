package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class ListVO<T> {


    @ApiModelProperty(name = "list", value = "数据", example = "")
    private List<T> list;

    public ListVO(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}

