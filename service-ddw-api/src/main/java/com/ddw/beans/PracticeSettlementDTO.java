package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeSettlementDTO {
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private int practiceId;
    @ApiModelProperty(name = "gameId",value = "原游戏编号（界面只读）",example = "1")
    private int gameId;
    @ApiModelProperty(name = "rankId",value = "原段位编号（界面只读）",example = "1")
    private int rankId;
    @ApiModelProperty(name = "star",value = "原星（界面只读）",example = "1")
    private int star;
    @ApiModelProperty(name = "nowRankId",value = "当前段位编号",example = "2")
    private int nowRankId;
    @ApiModelProperty(name = "nowStar",value = "当前星",example = "1")
    private int nowStar;

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

    public int getNowRankId() {
        return nowRankId;
    }

    public void setNowRankId(int nowRankId) {
        this.nowRankId = nowRankId;
    }

    public int getNowStar() {
        return nowStar;
    }

    public void setNowStar(int nowStar) {
        this.nowStar = nowStar;
    }
}
