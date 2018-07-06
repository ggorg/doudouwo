package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/1.
 */
@ApiModel
public class practiceGameVO {
    @ApiModelProperty(name="gameId",value="1",example="1")
    private int gameId;
    @ApiModelProperty(name="gameName",value="游戏名称",example="王者荣耀")
    private String gameName;
    @ApiModelProperty(name="rankId",value="1",example="xxxx")
    private int rankId;
    @ApiModelProperty(name="rank",value="段位名称",example="青铜")
    private String rank;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
