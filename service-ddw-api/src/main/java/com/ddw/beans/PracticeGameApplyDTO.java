package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeGameApplyDTO {
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private Integer practiceId;
    @ApiModelProperty(name = "gameId",value = "游戏编号",example = "1")
    private Integer gameId;
    @ApiModelProperty(name = "rankId",value = "段位编号",example = "1")
    private Integer rankId;
    @ApiModelProperty(name = "star",value = "星",example = "1")
    private Integer star;
    @ApiModelProperty(name = "targetRankId",value = "目标段位编号",example = "2")
    private Integer targetRankId;
    @ApiModelProperty(name = "targetStar",value = "目标星",example = "1")
    private Integer targetStar;

    public Integer getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Integer practiceId) {
        this.practiceId = practiceId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getTargetRankId() {
        return targetRankId;
    }

    public void setTargetRankId(Integer targetRankId) {
        this.targetRankId = targetRankId;
    }

    public Integer getTargetStar() {
        return targetStar;
    }

    public void setTargetStar(Integer targetStar) {
        this.targetStar = targetStar;
    }
}
