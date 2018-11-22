package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/9/3.
 */
@ApiModel(value="会员手机发送验证码",description="UserValidPhoneDTO")
public class UserValidPhoneDTO {
    @ApiModelProperty(name="telphone",value="手机号码",example="18566666666")
    private String telphone;
    @ApiModelProperty(name="type",value="发送验证码类型,1实名验证,2找回支付密码",example="发送验证码类型,1实名验证,2找回支付密码")
    private Integer type;

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
