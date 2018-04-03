package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

public class BaseDTO {
    @ApiModelProperty(name="token",value="令牌",example="xxxxxxxxxxxxx")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
