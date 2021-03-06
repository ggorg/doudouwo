package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveRadioPushVO {
    @ApiModelProperty(name="pushUrl",value="推流地址",example="rtmp://xxxxxx")
    private String pushUrl;//推流地址

    @ApiModelProperty(name="roomName",value="房名字",example="")
    @JsonProperty("roomName")
    private String spaceName;

    @ApiModelProperty(name="groupId",value="群ID",example="")
    private String groupId;

    @ApiModelProperty(name="backImgUrl",value="背景图片",example="背景图片")
    private String backImgUrl;


    public String getBackImgUrl() {
        return backImgUrl;
    }

    public void setBackImgUrl(String backImgUrl) {
        this.backImgUrl = backImgUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
