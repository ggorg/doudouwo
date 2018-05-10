package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingVO implements Serializable {

    @ApiModelProperty(name="openId",value="用户标识号",example="xxxx")
    private String openId;

    @ApiModelProperty(name="userName",value="用户名称",example="Xxx")
    private String userName;

    @ApiModelProperty(name="price",value="价格",example="100元")
    @JsonProperty
    private String price;

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
