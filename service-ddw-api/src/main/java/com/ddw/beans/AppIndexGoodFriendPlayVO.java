package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 好友约玩儿
 */
@ApiModel
public class AppIndexGoodFriendPlayVO {
    @ApiModelProperty(name="id",value="房间id",example="1")
    private Integer id;

    @ApiModelProperty(name="name",value="房间名称",example="牛人之说")
    private String dgName;

    @ApiModelProperty(name="tableNumber",value="桌号",example="808")
    private String dgTableNumber;

    @ApiModelProperty(name="peopleNumber",value="人数",example="1")
    private Integer peopleNumber;

    @ApiModelProperty(name="userHeadImgs",value="用户头像",example="1")
    private List userHeadImgs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgName() {
        return dgName;
    }

    public void setDgName(String dgName) {
        this.dgName = dgName;
    }

    public String getDgTableNumber() {
        return dgTableNumber;
    }

    public void setDgTableNumber(String dgTableNumber) {
        this.dgTableNumber = dgTableNumber;
    }

    public Integer getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(Integer peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public List getUserHeadImgs() {
        return userHeadImgs;
    }

    public void setUserHeadImgs(List userHeadImgs) {
        this.userHeadImgs = userHeadImgs;
    }
}
