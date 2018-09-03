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

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }
}
