package com.gen.common.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/5/14.
 */
public class RankPO {
    private int id;
    private int gameId;
    private Date createTime;

    private String rank;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
