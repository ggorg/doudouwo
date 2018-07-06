package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeEvaluationDTO {
    @ApiModelProperty(name = "gameId",value = "游戏ID,关联ddw_game游戏表",example = "1")
    private int gameId;
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private int practiceId;
    @ApiModelProperty(name = "star",value = "星,最低两颗星",example = "2")
    private int star;
    @ApiModelProperty(name = "describe",value = "评价",example = "这个代练好垃圾,掉星坑货")
    private String describe;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
