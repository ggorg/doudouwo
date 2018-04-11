package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AppIndexGoddessVO {
    @ApiModelProperty(name="id",value="女神id",example="1")
    private Integer id;

    @ApiModelProperty(name="nickName",value="昵称",example="女神")
    private String nickName;

    @ApiModelProperty(name="headImgUrl",value="图像",example="http.....")
    private String headImgUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
