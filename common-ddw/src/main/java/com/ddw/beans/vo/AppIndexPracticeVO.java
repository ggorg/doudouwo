package com.ddw.beans.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 代练
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexPracticeVO implements Serializable {
    private static final long serialVersionUID = 8329442349340665610L;
    @ApiModelProperty(name="userId",value="代练id,对应会员表id",example="1")
    private Integer userId;
    @ApiModelProperty(name="nickName",value="昵称",example="代练大神")
    private String nickName;
    @ApiModelProperty(name="imgUrl",value="相册第一张",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1529384238691&di=7e0db331f1a4b1d1b11e2643e8bb6ac3&imgtype=0&src=http%3A%2F%2Fi1.mopimg.cn%2Fimg%2Fdzh%2F2015-07%2F414%2F20150717120946794.jpg")
    private String imgUrl;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="pgradeName",value="代练等级名称",example="青铜")
    private String pgradeName;
    @ApiModelProperty(name="label",value="标签",example="1,2,3,4")
    private String label;
    @ApiModelProperty(name="ordersCount",value="接单数",example="2313")
    private Integer ordersCount;
    @ApiModelProperty(name="followed",value="已关注true,未关注false",example="true")
    private boolean followed;
    @ApiModelProperty(name="gameId",value="1",example="1")
    private int gameId;
    @ApiModelProperty(name="gameName",value="游戏名称",example="王者荣耀")
    private String gameName;
    @ApiModelProperty(name="rankId",value="1",example="xxxx")
    private int rankId;
    @ApiModelProperty(name="rank",value="段位名称",example="最强王者")
    private String rank;
    @ApiModelProperty(name="fans",value="粉丝数",example="16253")
    private Long fans;
    @ApiModelProperty(name = "star",value = "评价星",example = "2")
    private int star;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getPgradeName() {
        return pgradeName;
    }

    public void setPgradeName(String pgradeName) {
        this.pgradeName = pgradeName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
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

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
