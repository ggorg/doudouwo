package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/4/29.
 */
@ApiModel(value="会员实名认证展示用例",description="RealNameReviewVO")
public class RealNameReviewVO {
    @ApiModelProperty(name="userId",value="会员id",example="1")
    private Integer userId;
    @ApiModelProperty(name="realName",value="真实姓名",example="某某某")
    private String realName;
    @ApiModelProperty(name="idcard",value="身份证,女神,代练实名认证用",example="44xxxxxxxxxxxx")
    private String idcard;
    @ApiModelProperty(name="idcardFront",value="身份证正面上传图片",example="")
    private String idcardFront;
    @ApiModelProperty(name="idcardOpposite",value="身份证反面上传图片",example="")
    private String idcardOpposite;
    @ApiModelProperty(name="operator",value="审核人",example="admin")
    private String operator;
    @ApiModelProperty(name="status",value="审核状态，0未审核，1审核通过，2审核不通过",example="")
    private Integer status;
    @ApiModelProperty(name="describe",value="审核说明",example="")
    private String describe;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcardFront() {
        return idcardFront;
    }

    public void setIdcardFront(String idcardFront) {
        this.idcardFront = idcardFront;
    }

    public String getIdcardOpposite() {
        return idcardOpposite;
    }

    public void setIdcardOpposite(String idcardOpposite) {
        this.idcardOpposite = idcardOpposite;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
