package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginPublicDTO {
    @ApiModelProperty(name="accessToken",value="accessToken",example="accessToken")
    private String accessToken;
    @ApiModelProperty(name="openId",value="openId",example="openId")
    private String openId;

    @ApiModelProperty(name="registerType",value="注册类型1 微信注册,2 QQ注册",example="1")
    private Integer registerType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }

    @Override
    public String toString() {
        return "LoginPublicDTO{" +
                "accessToken='" + accessToken + '\'' +
                ", openId='" + openId + '\'' +
                ", registerType=" + registerType +
                '}';
    }
}
