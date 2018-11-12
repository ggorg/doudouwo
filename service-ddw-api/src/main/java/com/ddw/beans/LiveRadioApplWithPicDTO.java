package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

@ApiModel
public class LiveRadioApplWithPicDTO extends LiveRadioApplDTO {
    @ApiModelProperty(name = "liveHeadImg", value = "直播展示图片", example = "直播展示图片")
    private MultipartFile liveHeadImg;

    public MultipartFile getLiveHeadImg() {
        return liveHeadImg;
    }

    public void setLiveHeadImg(MultipartFile liveHeadImg) {
        this.liveHeadImg = liveHeadImg;
    }
}
