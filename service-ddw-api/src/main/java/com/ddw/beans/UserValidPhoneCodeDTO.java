package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/9/3.
 */
@ApiModel(value="会员手机验证码认证",description="UserValidPhoneDTO")
public class UserValidPhoneCodeDTO {
    @ApiModelProperty(name="telphone",value="手机号码",example="18566666666")
    private String telphone;
    @ApiModelProperty(name="code",value="验证码",example="123456")
    private String code;

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
