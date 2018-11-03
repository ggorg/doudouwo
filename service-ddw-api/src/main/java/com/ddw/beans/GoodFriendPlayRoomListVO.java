package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayRoomListVO  implements Serializable {


    private static final long serialVersionUID = -4540734804255986566L;
    @ApiModelProperty(name = "code", value = "code", example = "code")
    private Integer code;


    @ApiModelProperty(name = "roomImgIcon", value = "主题图", example = "主题图")
    private String roomImgIcon;

    @ApiModelProperty(name = "name", value = "主题名称", example = "主题名称")
    private String name;

    @ApiModelProperty(name = "tableNumber", value = "桌号", example = "桌号")
    private String tableNumber;
    @ApiModelProperty(name = "peopleNum", value = "人数", example = "人数")
    private Integer peopleNum;
    @ApiModelProperty(name = "peopleMaxNum", value = "人数上限", example = "人数上限")
    private Integer peopleMaxNum;

    @ApiModelProperty(name = "status", value = "预约中：0，约战中：1,约玩结束：22", example = "预约中：0，约战中：1,约玩结束：22")
    private Integer status;
    @ApiModelProperty(name = "type", value = "王者荣耀：1，刺激战场：2，全军出击：3，炉石传说：4,狼人杀:5,其它:100", example = "王者荣耀：1，刺激战场：2，全军出击：3，炉石传说：4,狼人杀:5,其它:100")
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getPeopleMaxNum() {
        return peopleMaxNum;
    }

    public void setPeopleMaxNum(Integer peopleMaxNum) {
        this.peopleMaxNum = peopleMaxNum;
    }

    public String getRoomImgIcon() {
        return roomImgIcon;
    }

    public void setRoomImgIcon(String roomImgIcon) {
        this.roomImgIcon = roomImgIcon;
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

