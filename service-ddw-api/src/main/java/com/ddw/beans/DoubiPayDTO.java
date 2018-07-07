package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;

@ApiModel
public class DoubiPayDTO {


    @ApiModelProperty(name="type",value="礼物：6",example="3")
    private Integer orderType;


    @ApiModelProperty(name="code",value="所购买的多个物品编号",example="1")
    private Integer code;

    @ApiModelProperty(name="num",value="数量",example="11")
    private Integer num;


    @ApiModelProperty(name="groupId",value="群ID（此参数用在房间购买礼物时候传，在商城购买礼物的不用传这个参数）",example="")
    private String groupId;

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
