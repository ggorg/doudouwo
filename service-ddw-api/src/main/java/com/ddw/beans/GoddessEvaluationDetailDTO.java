package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class GoddessEvaluationDetailDTO {
    @ApiModelProperty(name = "bidCode",value = "约玩Id",example = "约玩Id")
    private Integer bidCode;
    @ApiModelProperty(name = "star",value = "星,最低两颗星",example = "分数，1分：1，2分：2，3分：3，4分：4，5分：5")
    private Integer star;
    @ApiModelProperty(name = "describe",value = "评价",example = "这个女神好漂亮")
    private String describe;

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
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
