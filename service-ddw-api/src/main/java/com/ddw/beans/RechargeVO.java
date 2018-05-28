package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class RechargeVO {

    @ApiModelProperty(name="id",value="编号",example="1")
    private Integer id;

    @ApiModelProperty(name="name",value="名称",example="100元")
    private String name;

    @ApiModelProperty(name="desc",value="描述",example="xxxx")
    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
