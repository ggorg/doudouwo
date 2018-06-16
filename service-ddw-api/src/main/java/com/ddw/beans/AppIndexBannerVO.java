package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by Jacky on 2018/6/16.
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexBannerVO implements Serializable{
    @ApiModelProperty(name="name",value="名称",example="猫咪")
    private String name;
    @ApiModelProperty(name="picUrl",value="图片URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1529092528477&di=2871b05cd0eb41b63712bbe38ec2139f&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F014bc159770a29a8012193a3a295b0.jpg%402o.jpg")
    private String picUrl;
    @ApiModelProperty(name="url",value="跳转URL",example="http://doudouwo.cn/")
    private String url;
    @ApiModelProperty(name="describe",value="描述",example="这是一直可爱的猫咪")
    private String describe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
