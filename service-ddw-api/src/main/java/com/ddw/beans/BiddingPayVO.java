package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


@ApiModel
public class BiddingPayVO implements Serializable {

    @ApiModelProperty(name="msg",value="消息内容",example="恭喜。。。")
    private String msg;

    @ApiModelProperty(name="openid",value="用户标识号",example="")
    private String openid;

    @ApiModelProperty(name="needPayPrice",value="需要支付的金额",example="")
    private String needPayPrice;

    @ApiModelProperty(name="biddingId",value="某轮竞价的标识号",example="")
    private Integer biddingId;

    @ApiModelProperty(name="bidPrice",value="某轮竞价的金额",example="")
    private String  bidPrice;

    @Override
    public String toString() {
        return "BiddingPayVO{" +
                "msg='" + msg + '\'' +
                ", openid='" + openid + '\'' +
                ", needPayPrice=" + needPayPrice +
                ", biddingId=" + biddingId +
                ", bidPrice=" + bidPrice +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNeedPayPrice() {
        return needPayPrice;
    }

    public void setNeedPayPrice(String needPayPrice) {
        this.needPayPrice = needPayPrice;
    }

    public Integer getBiddingId() {
        return biddingId;
    }

    public void setBiddingId(Integer biddingId) {
        this.biddingId = biddingId;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }
}
