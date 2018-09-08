package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
public class OrderViewEvaluateDTO {

    @ApiModelProperty(name="score",value="分数",example="分数，1分：1，2分：2，3分：3，4分：4，5分：5")
    private Integer score;

    @ApiModelProperty(name="comment",value="评论",example="评论")
    private String comment;

    @ApiModelProperty(name="code",value="编号",example="编号")
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
