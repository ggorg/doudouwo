package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class GiftPacketVO {

    @ApiModelProperty(name="giftCode",value="礼物code",example="礼物code")
    private String giftCode;


    @ApiModelProperty(name="name",value="名称",example="名称")
    private String name;

    @ApiModelProperty(name="imgUrl",value="图片",example="yyyy-MM-dd HH:mm:ss")
    private String imgUrl;

    public String getGiftCode() {
        return giftCode;
    }

    public void setGiftCode(String giftCode) {
        this.giftCode = giftCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
