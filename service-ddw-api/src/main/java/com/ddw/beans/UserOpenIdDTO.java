package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="用户标识号",description="用户标识号")
public class UserOpenIdDTO {

    @ApiModelProperty(name="openId",value="用户标识号",example="xxxxxxxxx")
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
