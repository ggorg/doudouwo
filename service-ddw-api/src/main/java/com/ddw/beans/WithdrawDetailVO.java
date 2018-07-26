package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawDetailVO {

    @ApiModelProperty(name="money",value="提现金额",example="提现金额")
    private Integer money;
    @ApiModelProperty(name="type",value="类型：女神收益:1,代练收益：2",example="类型：女神收益:1,代练收益：2")
    private Integer type;

    @ApiModelProperty(name="accountTypeName",value="转账类型名称",example="转账类型名称")
    private String accountTypeName;
    @ApiModelProperty(name="accountType",value="转账类型,支付宝：1",example="转账类型,支付宝：1")
    private Integer accountType;

    @ApiModelProperty(name="reviewStatus",value="审批状态,待审核：0，审核通过：1，审核不通过：2",example="审批状态,待审核：0，审核通过：1，审核不通过：2")
    private Integer reviewStatus;
    @ApiModelProperty(name="reviewStatusName",value="审批状态名称",example="审批状态名称")
    private String reviewStatusName;
    @ApiModelProperty(name="reviewDesc",value="审批说明",example="审批说明")
    private String reviewDesc;

    @ApiModelProperty(name="applTime",value="申请时间",example="申请时间")
    private String applTime;

    @ApiModelProperty(name="reviewTime",value="审批时间",example="申请时间")
    private String reviewTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewStatusName() {
        return reviewStatusName;
    }

    public void setReviewStatusName(String reviewStatusName) {
        this.reviewStatusName = reviewStatusName;
    }

    public String getReviewDesc() {
        return reviewDesc;
    }

    public void setReviewDesc(String reviewDesc) {
        this.reviewDesc = reviewDesc;
    }

    public String getApplTime() {
        return applTime;
    }

    public void setApplTime(String applTime) {
        this.applTime = applTime;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }
}
