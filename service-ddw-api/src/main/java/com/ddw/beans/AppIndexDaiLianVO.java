package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 代练
 */
@ApiModel
public class AppIndexDaiLianVO {
    @ApiModelProperty(name="id",value="代练id",example="1")
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
