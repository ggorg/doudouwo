package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.objects.annotations.Property;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayCreateRoomDTO<T> {


    @ApiModelProperty(name = "roomImg", value = "主题图", example = "主题图")
    private  MultipartFile roomImg;

    @ApiModelProperty(name = "name", value = "主题名称", example = "主题名称")
    private String name;
    @ApiModelProperty(name = "type", value = "王者荣耀：1，刺激战场：2，全军出击：3，炉石传说：4,狼人杀:5,其它:100", example = "王者荣耀：1，刺激战场：2，全军出击：3，炉石传说：4,狼人杀:5,其它:100")
    private Integer type;
    @ApiModelProperty(name = "tableCode", value = "桌号code", example = "桌号code")
    private Integer tableCode;

    @ApiModelProperty(name = "peopleMaxNum", value = "人数上限", example = "人数上限")
    private Integer peopleMaxNum;
    @ApiModelProperty(name = "chatRoomEndTime", value = "结束时间", example = "结束时间")
    private String chatRoomEndTime;
    @ApiModelProperty(name = "describe", value = "描述，500字上限", example = "描述，500字上限")
    private String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public MultipartFile getRoomImg() {
        return roomImg;
    }

    public void setRoomImg(MultipartFile roomImg) {
        this.roomImg = roomImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTableCode() {
        return tableCode;
    }

    public void setTableCode(Integer tableCode) {
        this.tableCode = tableCode;
    }

    public Integer getPeopleMaxNum() {
        return peopleMaxNum;
    }

    public void setPeopleMaxNum(Integer peopleMaxNum) {
        this.peopleMaxNum = peopleMaxNum;
    }

    public String getChatRoomEndTime() {
        return chatRoomEndTime;
    }

    public void setChatRoomEndTime(String chatRoomEndTime) {
        this.chatRoomEndTime = chatRoomEndTime;
    }
}

