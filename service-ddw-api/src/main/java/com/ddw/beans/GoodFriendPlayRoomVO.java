package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayRoomVO<T> {


    @ApiModelProperty(name = "code", value = "code", example = "code")
    private Integer code;


    @ApiModelProperty(name = "roomImg", value = "背景图", example = "背景图")
    private String roomImg;

    @ApiModelProperty(name = "name", value = "主题名称", example = "主题名称")
    private String name;

    @ApiModelProperty(name = "tableNumber", value = "桌号", example = "桌号")
    private String tableNumber;
    @ApiModelProperty(name = "status", value = "预约中：0，约战中：1", example = "预约中：0，约战中：1,约玩审核中(可就坐)：20，已拒绝：21")
    private Integer status;


    @ApiModelProperty(name = "peopleMaxNum", value = "人数上限", example = "人数上限")
    private Integer peopleMaxNum;

    @ApiModelProperty(name = "peopleNum", value = "人数", example = "人数")
    private Integer peopleNum;
    @ApiModelProperty(name = "groupId", value = "群ID" , example = "群ID")
    private String groupId;

    @ApiModelProperty(name = "开桌成员列表", value = "开桌成员列表" , example = "开桌成员列表")
    private List memberList;

    public List getMemberList() {
        return memberList;
    }

    public void setMemberList(List memberList) {
        this.memberList = memberList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getPeopleMaxNum() {
        return peopleMaxNum;
    }

    public void setPeopleMaxNum(Integer peopleMaxNum) {
        this.peopleMaxNum = peopleMaxNum;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getRoomImg() {
        return roomImg;
    }

    public void setRoomImg(String roomImg) {
        this.roomImg = roomImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }


}

