package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingVO implements Serializable {

    private static final long serialVersionUID = 7613354659113426114L;
    @ApiModelProperty(name="openId",value="用户标识号",example="xxxx")
    private String openId;

    @ApiModelProperty(name="userName",value="用户名称",example="Xxx")
    private String userName;

    @ApiModelProperty(name="price",value="价格，单位分",example="10000")
    @JsonProperty
    private String price;

    @ApiModelProperty(name="time",value="时长，单位：分",example="1")
    private Integer time;

    @JsonIgnore
    private String bidEndTime;

    public String getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(String bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    @JsonIgnore
    private Integer userId;



    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
