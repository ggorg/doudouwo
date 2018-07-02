package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingRenewVO implements Serializable {

    private static final long serialVersionUID = 6136443195910503519L;
    @ApiModelProperty(name="bidCode",value="竞价code",example="xxxx")
    private Integer bidCode;


    @ApiModelProperty(name="price",value="续费价格",example="续费价格")
    private Integer price;
     @ApiModelProperty(name="statusMsg",value="状态值",example="10000")
    private String statusMsg;

    @ApiModelProperty(name="status",value="状态，已结束：8，约玩中：5",example="状态，已结束：8，约玩中：5")
    private Integer status;

    @ApiModelProperty(name="endTime",value="约玩结束时间",example="约玩结束时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public Integer getBidCode() {
        return bidCode;
    }

    public void setBidCode(Integer bidCode) {
        this.bidCode = bidCode;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
