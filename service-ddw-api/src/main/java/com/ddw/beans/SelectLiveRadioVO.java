package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectLiveRadioVO {

    @ApiModelProperty(name="pullUrl",value="播放地址",example="rtmp://xxxx")
    public String pullUrl;

    @ApiModelProperty(name="groupId",value="聊天群组",example="xxxxxxg")
    public String groupId;

    @ApiModelProperty(name="nickName",value="女神昵称",example="xxxxxxg")
    public String nickName;
    @ApiModelProperty(name="headImgUrl",value="女神头像",example="xxxxxxg")
    public String headImgUrl;

    @ApiModelProperty(name="attention",value="未关注：0，已关注：1",example="0")
    private Integer attention;

    @ApiModelProperty(name="goddessCode",value="女神id",example="0")
    private Integer goddessCode;

    @ApiModelProperty(name="openId",value="openId",example="openId")
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getGoddessCode() {
        return goddessCode;
    }

    public void setGoddessCode(Integer goddessCode) {
        this.goddessCode = goddessCode;
    }

    public Integer getAttention() {
        return attention;
    }

    public void setAttention(Integer attention) {
        this.attention = attention;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getPullUrl() {
        return pullUrl;
    }

    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "SelectLiveRadioVO{" +
                "pullUrl='" + pullUrl + '\'' +
                ", groupId='" + groupId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", attention=" + attention +
                '}';
    }
}
