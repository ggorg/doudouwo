package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawWayVO {

    @ApiModelProperty(name="code",value="code",example="1")
    private Integer code;

    @ApiModelProperty(name="accountNoStr",value="转账账号",example="转账账号")
    private String accountNoStr;
    @ApiModelProperty(name="accountType",value="转账类型,支付宝：1",example="转账类型,支付宝：1")
    private Integer accountType;

    @ApiModelProperty(name="accountRealName",value="账号人名称",example="账号人名称")
    private String accountRealName;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getAccountNoStr() {
        return accountNoStr;
    }

    public void setAccountNoStr(String accountNoStr) {
        this.accountNoStr = accountNoStr;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountRealName() {
        return accountRealName;
    }

    public void setAccountRealName(String accountRealName) {
        this.accountRealName = accountRealName;
    }
}
