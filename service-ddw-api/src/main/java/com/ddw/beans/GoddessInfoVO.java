package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

public class GoddessInfoVO {

    @ApiModelProperty(name="headImg",value="头像",example="http://")
    private String headImg;

    @ApiModelProperty(name="name",value="昵称",example="张三")
    private String name;
    @ApiModelProperty(name="fansNum",value="粉丝数量",example="粉丝数量")
    private Integer fansNum;
    @ApiModelProperty(name="attentionNum",value="关注数量",example="关注数量")
    private Integer attentionNum;

    @ApiModelProperty(name="contributeNum",value="贡献数量",example="贡献数量")
    private Integer contributeNum;

    @ApiModelProperty(name="isAttenion",value="是否关注",example="是否关注(0：未关注，1：已关注)")
    private Integer isAttenion;

    public Integer getIsAttenion() {
        return isAttenion;
    }

    public void setIsAttenion(Integer isAttenion) {
        this.isAttenion = isAttenion;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }

    public Integer getAttentionNum() {
        return attentionNum;
    }

    public void setAttentionNum(Integer attentionNum) {
        this.attentionNum = attentionNum;
    }

    public Integer getContributeNum() {
        return contributeNum;
    }

    public void setContributeNum(Integer contributeNum) {
        this.contributeNum = contributeNum;
    }
}
