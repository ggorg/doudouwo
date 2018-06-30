package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletUpdatePayPwdDTO {
    @ApiModelProperty(name="oldPwd",value="钱包原支付密码,首次原密码为空",example="111111")
    private String oldPwd;
    @ApiModelProperty(name="newPwd",value="新密码",example="123456")
    private String newPwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }
}
