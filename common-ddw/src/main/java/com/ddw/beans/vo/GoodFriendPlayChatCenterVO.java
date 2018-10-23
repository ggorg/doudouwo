package com.ddw.beans.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayChatCenterVO<T> implements Serializable {


    private static final long serialVersionUID = -471492084530105300L;
    @ApiModelProperty(name = "code", value = "code", example = "code")
    @JsonProperty("code")
    private Integer id;


    @ApiModelProperty(name = "bgImgUrl", value = "背景图", example = "背景图")
    private String bgImgUrl;

    @ApiModelProperty(name = "群ID", value = "群ID", example = "群ID")
    private String groupId;

    @ApiModelProperty(name = "roomList", value = "小房间列表", example = "小房间列表")
    private List<T> roomList;
    /*@ApiModelProperty(name = "offLinelist", value = "约战中列表", example = "约战中列表")
    private List<T> offLinelist;
    @ApiModelProperty(name = "onLineList", value = "预约中列表", example = "预约中列表")
    private List<T> onLineList;

    public List<T> getOffLinelist() {
        return offLinelist;
    }

    public void setOffLinelist(List<T> offLinelist) {
        this.offLinelist = offLinelist;
    }

    public List<T> getOnLineList() {
        return onLineList;
    }

    public void setOnLineList(List<T> onLineList) {
        this.onLineList = onLineList;
    }*/

    public List<T> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<T> roomList) {
        this.roomList = roomList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

