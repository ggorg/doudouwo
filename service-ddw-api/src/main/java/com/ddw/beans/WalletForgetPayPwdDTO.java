package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletForgetPayPwdDTO {
    @ApiModelProperty(name="telphone",value="手机号码",example="18566666666")
    private String telphone;
    @ApiModelProperty(name="code",value="验证码",example="123456")
    private String code;
    @ApiModelProperty(name="newPwd",value="新密码",example="123456")
    private String newPwd;

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

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }
}
