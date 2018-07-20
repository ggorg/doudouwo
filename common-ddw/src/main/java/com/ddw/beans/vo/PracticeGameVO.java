package com.ddw.beans.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by Jacky on 2018/7/1.
 */
@ApiModel
public class PracticeGameVO implements Serializable {

    private static final long serialVersionUID = 2509953261559274945L;
    @ApiModelProperty(name="gameId",value="1",example="1")
    private int gameId;
    @ApiModelProperty(name="gameName",value="游戏名称",example="王者荣耀")
    private String gameName;
    @ApiModelProperty(name="rankId",value="1",example="xxxx")
    private int rankId;
    @ApiModelProperty(name="rank",value="段位名称",example="青铜")
    private String rank;
    @ApiModelProperty(name="appointment",value="预约开关，1开启，2代练中，0关闭'",example="1")
    private int appointment;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public int getAppointment() {
        return appointment;
    }

    public void setAppointment(int appointment) {
        this.appointment = appointment;
    }
}
