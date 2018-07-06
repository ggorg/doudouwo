package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeGameApplyDTO {
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private int practiceId;
    @ApiModelProperty(name = "gameId",value = "游戏编号",example = "1")
    private int gameId;
    @ApiModelProperty(name = "rankId",value = "段位编号",example = "1")
    private int rankId;
    @ApiModelProperty(name = "star",value = "星",example = "1")
    private int star;
    @ApiModelProperty(name = "targetrankId",value = "目标段位编号",example = "2")
    private int targetrankId;
    @ApiModelProperty(name = "targetStar",value = "目标星",example = "1")
    private int targetStar;

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getTargetrankId() {
        return targetrankId;
    }

    public void setTargetrankId(int targetrankId) {
        this.targetrankId = targetrankId;
    }

    public int getTargetStar() {
        return targetStar;
    }

    public void setTargetStar(int targetStar) {
        this.targetStar = targetStar;
    }
}
