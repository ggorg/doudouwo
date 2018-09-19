package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsListVO<T> {

    @ApiModelProperty(name = "headUrl", value = "头像", example = "头像")
    private String headUrl;

    @ApiModelProperty(name = "bannerUrl", value = "横幅", example = "横幅")
    private String bannerUrl;

    @ApiModelProperty(name = "name", value = "门店名称", example = "门店名称")
    private String name;

    @ApiModelProperty(name = "address", value = "地址", example = "地址")
    private String address;

    @ApiModelProperty(name = "desc", value = "描述", example = "描述")
    private String desc;

    @ApiModelProperty(name = "list", value = "数据", example = "")
    private List<T> list;

    public GoodsListVO(List<T> list) {
        this.list = list;
    }
    public GoodsListVO() {

    }
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ListVO{" +
                "list=" + list +
                '}';
    }
}

