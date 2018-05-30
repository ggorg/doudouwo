package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class GoodsInfoVO<T> {

    @ApiModelProperty(name="name",value="商品名称",example="奶茶")
    @JsonProperty("name")
    private String dgTitle;

    @ApiModelProperty(name="imgUrl",value="商品照片",example="http://...")
    @JsonProperty("imgUrl")
    private String fileImgIcoPath;

    @ApiModelProperty(name="detail",value="描述",example="")
    @JsonProperty("detail")
    private String dgDetail;

    @ApiModelProperty(name="likeNum",value="点赞数量",example="0")
    private Integer likeNum;

    @ApiModelProperty(name="monthNum",value="点赞数量",example="0")
    private Integer monthNum;

    private List<T> pruduct;

    public List<T> getPruduct() {
        return pruduct;
    }

    public void setPruduct(List<T> pruduct) {
        this.pruduct = pruduct;
    }

    public String getDgTitle() {
        return dgTitle;
    }

    public void setDgTitle(String dgTitle) {
        this.dgTitle = dgTitle;
    }

    public String getFileImgIcoPath() {
        return fileImgIcoPath;
    }

    public void setFileImgIcoPath(String fileImgIcoPath) {
        this.fileImgIcoPath = fileImgIcoPath;
    }

    public String getDgDetail() {
        return dgDetail;
    }

    public void setDgDetail(String dgDetail) {
        this.dgDetail = dgDetail;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Integer getMonthNum() {
        return monthNum;
    }

    public void setMonthNum(Integer monthNum) {
        this.monthNum = monthNum;
    }
}
