package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class RankingListVO implements Serializable {
    private static final long serialVersionUID = 5986934177743999641L;
    @ApiModelProperty(name="nickName",value="昵称",example="1")
    private String nickName;
    @ApiModelProperty(name="consumePrice",value="消费总额",example="1")
    private Integer consumePrice;

    @ApiModelProperty(name="headImgUrl",value="头像url",example="http://xxxx")
    private String headImgUrl;
    @ApiModelProperty(name="level",value="级别",example="级别")
    private String level;
    @ApiModelProperty(name="gradeName",value="级别名称",example="级别名称")
    private String gradeName;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

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
