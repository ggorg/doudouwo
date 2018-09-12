package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicsVO {


    @ApiModelProperty(name="title",value="标题",example="标题")
    private String title;

    @ApiModelProperty(name="dynType",value="动态类型",example="动态类型，文字：1，图文：2")
    private String dynType;

    @ApiModelProperty(name="Imgs",value="图片",example="https://xxxx")
    private String Imgs;
    @ApiModelProperty(name="createTime",value="创建时间",example="创建时间")
    private String createTime;
    @ApiModelProperty(name="useTime",value="用时",example="16分51秒")
    private String useTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDynType() {
        return dynType;
    }

    public void setDynType(String dynType) {
        this.dynType = dynType;
    }

    public String getImgs() {
        return Imgs;
    }

    public void setImgs(String imgs) {
        Imgs = imgs;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
