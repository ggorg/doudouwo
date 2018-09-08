package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderViewEvaluateStoreListVO extends OrderViewEvaluateListVO  {


    @ApiModelProperty(name="img",value="照片",example="照片")
    private String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
