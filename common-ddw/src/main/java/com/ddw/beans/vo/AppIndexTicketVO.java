package com.ddw.beans.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 门票
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexTicketVO implements Serializable {

    @JsonProperty("name")
    @ApiModelProperty(name="name",value="门票名称",example="门票")
    private String name;

    @JsonProperty("price")
    @ApiModelProperty(name="price",value="价格",example="10元")
    private Integer price;

    @JsonProperty("code")
    @ApiModelProperty(name="code",value="编号",example="xxxxxxxx")
    private Integer code;

    @JsonProperty("activeTime")
    @ApiModelProperty(name="activeTime",value="开放时间",example="09:00:00 - 12:00:00")
    private String activeTime;

    @JsonProperty("typeName")
    @ApiModelProperty(name="typeName",value="商品类型",example="白天票")
    private String typeName;

    @JsonProperty("type")
    @ApiModelProperty(name="type",value="商品类型编号",example="0")
    private Integer type;


    @JsonProperty("actPrice")
    @ApiModelProperty(name="actPrice",value="活动价格",example="10000")
    private Integer actPrice;

    @JsonProperty("desc")
    @ApiModelProperty(name="desc",value="描述",example="烂票")
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getActPrice() {
        return actPrice;
    }

    public void setActPrice(Integer actPrice) {
        this.actPrice = actPrice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
