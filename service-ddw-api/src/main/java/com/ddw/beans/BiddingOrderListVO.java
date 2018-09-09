package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingOrderListVO implements Serializable {
    private static final long serialVersionUID = 5418624276183166858L;
    //t1.createTime,t1.price,t1.endTime,t1.times,t1.userId,t1.groupId,ct0.headImgUrl,ct0.nickName
    @ApiModelProperty(name="price",value="金额",example="金额")
    private Integer price;

    @ApiModelProperty(name="status",value="状态",example="状态")
    private Integer status;

    @ApiModelProperty(name="statusMsg",value="状态描述",example="状态描述")
    private String statusMsg;

    @ApiModelProperty(name="nickName",value="用户名称",example="用户名称")
    private String nickName;

    @ApiModelProperty(name="time",value="时长，单位：分",example="时长，单位：分")
    private Integer time;

    @ApiModelProperty(name="endTime",value="结束时间",example="结束时间")
    private String endTime;

    @ApiModelProperty(name="startTime",value="开始时间",example="开始时间")
    private String startTime;

    @ApiModelProperty(name="payEndTime",value="支付结束时间",example="支付结束时间")
    private String payEndTime;
    @ApiModelProperty(name="makeSureEndTime",value="确认结束时间",example="确认结束时间")
    private String makeSureEndTime;
    @ApiModelProperty(name="createTime",value="创建时间",example="创建时间")
    private String createTime;
    @ApiModelProperty(name="headImgUrl",value="头像",example="头像")
    private String headImgUrl;
    @ApiModelProperty(name="openid",value="openid",example="openid")
    private String openid;
    @ApiModelProperty(name="bidCode",value="竞价code",example="bidCode")
    private Integer bidCode;
    @ApiModelProperty(name="isEvaluate",value="评价",example="是否已评价,未评价：0，已评价：1")
    private Integer isEvaluate;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getIsEvaluate() {
        return isEvaluate;
    }

    public void setIsEvaluate(Integer isEvaluate) {
        this.isEvaluate = isEvaluate;
    }

    public String getMakeSureEndTime() {
        return makeSureEndTime;
    }

    public void setMakeSureEndTime(String makeSureEndTime) {
        this.makeSureEndTime = makeSureEndTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(String payEndTime) {
        this.payEndTime = payEndTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
    }
}
