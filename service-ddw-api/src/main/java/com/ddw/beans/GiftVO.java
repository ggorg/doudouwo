package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel
public class GiftVO implements Serializable {
    @ApiModelProperty(name="code",value="礼物编号",example="1")
    @JsonProperty("code")
    private Integer id;

    @ApiModelProperty(name="name",value="礼物名称",example="kiss")
    @JsonProperty("name")
    private String dgName;

    @ApiModelProperty(name="price",value="价格,单位：分",example="100")
    @JsonProperty("price")
    private Integer dgPrice;
    @ApiModelProperty(name="actPrice",value="活动价格,单位：分",example="100")
    @JsonProperty("actPrice")
    private Integer dgActPrice;

    @ApiModelProperty(name="imgUrl",value="图片url",example="http://xxxx")
    @JsonProperty("imgUrl")
    private String dgImgPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgName() {
        return dgName;
    }

    public void setDgName(String dgName) {
        this.dgName = dgName;
    }

    public Integer getDgPrice() {
        return dgPrice;
    }

    public void setDgPrice(Integer dgPrice) {
        this.dgPrice = dgPrice;
    }

    public Integer getDgActPrice() {
        return dgActPrice;
    }

    public void setDgActPrice(Integer dgActPrice) {
        this.dgActPrice = dgActPrice;
    }

    public String getDgImgPath() {
        return dgImgPath;
    }

    public void setDgImgPath(String dgImgPath) {
        this.dgImgPath = dgImgPath;
    }
}
