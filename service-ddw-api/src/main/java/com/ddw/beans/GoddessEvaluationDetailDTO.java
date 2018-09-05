package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class GoddessEvaluationDetailDTO {
    @ApiModelProperty(name = "userId",value = "会员编号",example = "1")
    private Integer userId;
    @ApiModelProperty(name = "goddessId",value = "女神编号",example = "1")
    private Integer goddessId;
    @ApiModelProperty(name = "star",value = "星,最低两颗星",example = "2")
    private Integer star;
    @ApiModelProperty(name = "describe",value = "评价",example = "这个女神好漂亮")
    private String describe;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(Integer goddessId) {
        this.goddessId = goddessId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
