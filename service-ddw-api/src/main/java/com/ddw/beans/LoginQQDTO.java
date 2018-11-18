package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginQQDTO {
    @ApiModelProperty(name="openid",value="openid",example="openid")
    private String openid;
    @ApiModelProperty(name="openkey",value="openkey",example="openkey")
    private String openkey;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenkey() {
        return openkey;
    }

    public void setOpenkey(String openkey) {
        this.openkey = openkey;
    }
}
