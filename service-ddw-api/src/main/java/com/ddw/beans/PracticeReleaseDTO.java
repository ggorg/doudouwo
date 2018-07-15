package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class PracticeReleaseDTO {
    @ApiModelProperty(name = "gameId",value = "游戏ID,关联ddw_game游戏表",example = "1")
    private int gameId;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

}
