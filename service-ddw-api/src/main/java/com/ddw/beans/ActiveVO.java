package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveVO<T> {


    @ApiModelProperty(name="title",value="标题",example="标题")
    private String title;

    @ApiModelProperty(name="imgUrl",value="图片地址",example="图片地址")
    private String imgUrl;

    @ApiModelProperty(name="activeTime",value="活动时间",example="活动时间")
    private String activeTime;

    @ApiModelProperty(name="jumpUrl",value="跳转地址",example="跳转地址")
    private String jumpUrl;

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }
}
