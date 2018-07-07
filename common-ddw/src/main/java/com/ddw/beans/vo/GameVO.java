package com.ddw.beans.vo;

import com.ddw.beans.RankPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jacky on 2018/7/6.
 */
@ApiModel
public class GameVO implements Serializable{
    private static final long serialVersionUID = 5244406517903017996L;

    @ApiModelProperty(name="id",value="游戏编号",example="1")
    private int id;
    @ApiModelProperty(name="gameName",value="游戏名称",example="王者荣耀")
    private String gameName;
    @ApiModelProperty(name="rankList",value="段位",example="{\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"rank\": \"青铜\",\n" +
            "                    \"star\": \"5\"\n" +
            "                }")
    private List<RankPO> rankList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<RankPO> getRankList() {
        return rankList;
    }

    public void setRankList(List<RankPO> rankList) {
        this.rankList = rankList;
    }
}
