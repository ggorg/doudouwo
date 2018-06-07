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

    @ApiModelProperty(name="code",value="某轮竞价的编号",example="")
    private Integer code;

    @ApiModelProperty(name="bidPrice",value="某轮竞价的金额",example="")
    private String  bidPrice;

    @ApiModelProperty(name="time",value="时长，单位：分钟",example="")
    private Integer  time;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
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



    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }
}
