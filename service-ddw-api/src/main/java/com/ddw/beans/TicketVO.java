package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class TicketVO implements Serializable {
    @ApiModelProperty(name="code",value="门票编号",example="1")
    @JsonProperty("code")
    private Integer id;

    @ApiModelProperty(name="name",value="门票名称",example="kiss")
    @JsonProperty("name")
    private String dtName;

    @ApiModelProperty(name="price",value="价格,单位：分",example="100")
    @JsonProperty("price")
    private Integer dtPrice;
    @ApiModelProperty(name="actPrice",value="活动价格,单位：分",example="100")
    @JsonProperty("actPrice")
    private Integer dtActPrice;

    @ApiModelProperty(name="desc",value="描述",example="xxxx")
    @JsonProperty("desc")
    private String dtDesc;
    @ApiModelProperty(name="type",value="类型，白天票：0，晚上票：1，通票：2，狼人杀：3",example="xxxx")
    @JsonProperty("type")
    private String dtType;

    @ApiModelProperty(name="typeName",value="类型名称",example="白天票")
    @JsonProperty("typeName")
    private String typeName;



    @ApiModelProperty(name="activeTime",value="有效时间",example="9:00:00-12:00:00")
    @JsonProperty("activeTime")
    private String dtActiveTime;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDtName() {
        return dtName;
    }

    public void setDtName(String dtName) {
        this.dtName = dtName;
    }

    public Integer getDtPrice() {
        return dtPrice;
    }

    public void setDtPrice(Integer dtPrice) {
        this.dtPrice = dtPrice;
    }

    public Integer getDtActPrice() {
        return dtActPrice;
    }

    public void setDtActPrice(Integer dtActPrice) {
        this.dtActPrice = dtActPrice;
    }

    public String getDtDesc() {
        return dtDesc;
    }

    public void setDtDesc(String dtDesc) {
        this.dtDesc = dtDesc;
    }

    public String getDtType() {
        return dtType;
    }

    public void setDtType(String dtType) {
        this.dtType = dtType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}