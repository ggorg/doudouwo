package com.gen.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="用例对像",description="用例对像DemoVo")
public class DemoVo extends BaseVo {

    @ApiModelProperty(name="name",value="姓名",example="某某某")
    private String name;

    @ApiModelProperty(name="id",value="主键",example="1")
    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DemoVo{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
