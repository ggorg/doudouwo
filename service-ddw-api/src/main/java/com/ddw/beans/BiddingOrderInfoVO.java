package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingOrderInfoVO implements Serializable {

    @ApiModelProperty(name="openId",value="用户标识号",example="用户标识号")
    private String openId;

    @ApiModelProperty(name="status",value="状态",example="状态")
    private Integer status;

    @ApiModelProperty(name="statusMsg",value="状态描述",example="状态描述")
    private String statusMsg;

    @ApiModelProperty(name="userName",value="用户名称",example="用户名称")
    private String userName;

    @ApiModelProperty(name="time",value="时长，单位：分",example="时长，单位：分")
    private Integer time;

    @ApiModelProperty(name="price",value="价格",example="价格")
    private Integer price;

    @ApiModelProperty(name="endTime",value="结束时间",example="结束时间")
    private String endTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
