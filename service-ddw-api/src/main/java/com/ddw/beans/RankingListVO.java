package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class RankingListVO implements Serializable {
    @ApiModelProperty(name="nickName",value="昵称",example="1")
    private String nickName;
    @ApiModelProperty(name="consumePrice",value="消费总额",example="1")
    private Integer consumePrice;

    @ApiModelProperty(name="headImgUrl",value="头像url",example="http://xxxx")
    private String headImgUrl;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getConsumePrice() {
        return consumePrice;
    }

    public void setConsumePrice(Integer consumePrice) {
        this.consumePrice = consumePrice;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
