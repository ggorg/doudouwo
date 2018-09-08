package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderViewEvaluateListVO {


    @ApiModelProperty(name="comment",value="评论",example="评论")
    private String comment;

    @ApiModelProperty(name="userName",value="用户名称",example="用户名称")
    private String userName;

    @ApiModelProperty(name="userHeadImg",value="用户头像",example="用户头像")
    private String userHeadImg;

    @ApiModelProperty(name="time",value="评论时间",example="评论时间")
    private String time;

    @ApiModelProperty(name="score",value="评分",example="评分")
    private Integer score;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
