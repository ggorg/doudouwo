package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeRefundVO {
    @ApiModelProperty(name = "orderId",value = "订单编号",example = "1")
    private Integer orderId;
    @ApiModelProperty(name = "reason",value = "退款原因",example = "态度恶劣")
    private String reason;
    @ApiModelProperty(name = "describe",value = "描述",example = "看不顺眼")
    private String describe;
    @ApiModelProperty(name = "feedback",value = "拒绝原因反馈",example = "代练操作合规,未有明显出格行为")
    private String feedback;
    @ApiModelProperty(name = "picUrl",value = "凭证",example = "")
    private String picUrl;
    @ApiModelProperty(name = "status",value = "退款状态，0未退款，1成功,2拒绝",example = "2")
    private Integer status;
    @ApiModelProperty(name = "createTime",value = "创建日期",example = "")
    private Date createTime;
    @ApiModelProperty(name = "updateTime",value = "更新日期",example = "")
    private Date updateTime;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
