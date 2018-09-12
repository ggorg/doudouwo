package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/5/12.
 */
@ApiModel
public class ReviewPracticeVO {
    @ApiModelProperty(name="gameId",value="游戏编号",example="1")
    private Integer gameId;
    @ApiModelProperty(name="gameName",value="游戏名",example="王者荣耀")
    private String gameName;
    @ApiModelProperty(name="rankId",value="段位编号",example="7")
    private Integer rankId;
    @ApiModelProperty(name="rank",value="段位名称",example="铂金")
    private String rank;
    @ApiModelProperty(name="picUrl1",value="认证图片1 URL",example="")
    private String picUrl1;
    @ApiModelProperty(name="picUrl2",value="认证图片2 URL",example="")
    private String picUrl2;
    @ApiModelProperty(name="picUrl3",value="认证图片3 URL",example="")
    private String picUrl3;
    @ApiModelProperty(name="status",value="审核状态，0未审核，1审核通过，2审核拒绝",example="2")
    private Integer status;
    @ApiModelProperty(name="describe",value="审核说明",example="请上传真实的游戏凭证")
    private String describe;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPicUrl1() {
        return picUrl1;
    }

    public void setPicUrl1(String picUrl1) {
        this.picUrl1 = picUrl1;
    }

    public String getPicUrl2() {
        return picUrl2;
    }

    public void setPicUrl2(String picUrl2) {
        this.picUrl2 = picUrl2;
    }

    public String getPicUrl3() {
        return picUrl3;
    }

    public void setPicUrl3(String picUrl3) {
        this.picUrl3 = picUrl3;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
