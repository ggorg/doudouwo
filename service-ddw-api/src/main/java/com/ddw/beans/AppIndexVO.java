package com.ddw.beans;

import com.ddw.beans.vo.AppIndexBannerVO;
import com.ddw.beans.vo.AppIndexButtonVO;
import com.ddw.beans.vo.AppIndexPracticeVO;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.AppIndexTicketVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexVO implements Serializable {
    private static final long serialVersionUID = 9026400385035060991L;
    @ApiModelProperty(name="topImgsList",value="顶部图片",example="")
    public List<AppIndexTopImgsVO> topImgsList ;

    @ApiModelProperty(name="buttonList",value="按钮",example="")
    public List<AppIndexButtonVO> buttonList;

    @ApiModelProperty(name="ticketList",value="门票",example="")
    public List<AppIndexTicketVO> ticketList;

    @ApiModelProperty(name="goddessList",value="女神",example="")
    public List<AppIndexGoddessVO> goddessList;

    @ApiModelProperty(name="goodFriendPlayList",value="好友约玩儿",example="")
    public List<AppIndexGoodFriendPlayVO> goodFriendPlayList;

    @ApiModelProperty(name="practiceList",value="代练",example="")
    public List<AppIndexPracticeVO> practiceList;

    @ApiModelProperty(name="bannerList",value="banner",example="")
    public List<AppIndexBannerVO>bannerList;

    public List<AppIndexTopImgsVO> getTopImgsList() {
        return topImgsList;
    }

    public void setTopImgsList(List<AppIndexTopImgsVO> topImgsList) {
        this.topImgsList = topImgsList;
    }

    public List<AppIndexButtonVO> getButtonList() {
        return buttonList;
    }

    public void setButtonList(List<AppIndexButtonVO> buttonList) {
        this.buttonList = buttonList;
    }

    public List<AppIndexTicketVO> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<AppIndexTicketVO> ticketList) {
        this.ticketList = ticketList;
    }

    public List<AppIndexGoddessVO> getGoddessList() {
        return goddessList;
    }

    public void setGoddessList(List<AppIndexGoddessVO> goddessList) {
        this.goddessList = goddessList;
    }

    public List<AppIndexGoodFriendPlayVO> getGoodFriendPlayList() {
        return goodFriendPlayList;
    }

    public void setGoodFriendPlayList(List<AppIndexGoodFriendPlayVO> goodFriendPlayList) {
        this.goodFriendPlayList = goodFriendPlayList;
    }

    public List<AppIndexPracticeVO> getPracticeList() {
        return practiceList;
    }

    public void setPracticeList(List<AppIndexPracticeVO> practiceList) {
        this.practiceList = practiceList;
    }

    public List<AppIndexBannerVO> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<AppIndexBannerVO> bannerList) {
        this.bannerList = bannerList;
    }
}
