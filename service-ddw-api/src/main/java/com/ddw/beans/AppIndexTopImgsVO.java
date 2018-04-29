package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页顶部图片
 */
@ApiModel
public class AppIndexTopImgsVO {
    @ApiModelProperty(name="imgUrl",value="图片地址",example="http:/xxxxxxxxxxxx")
    @JsonProperty("imgUrl")
    private String dtImgPath;

    @JsonProperty("targetUrl")
    @ApiModelProperty(name="targetUrl",value="活动地址",example="http:/xxxxxxxxxxxx")
    private String dtTargetPath;

    @JsonProperty("title")
    @ApiModelProperty(name="title",value="标题 ",example="xxxxxx")
    private String dtTitle;

    public String getDtImgPath() {
        return dtImgPath;
    }

    public void setDtImgPath(String dtImgPath) {
        this.dtImgPath = dtImgPath;
    }

    public String getDtTargetPath() {
        return dtTargetPath;
    }

    public void setDtTargetPath(String dtTargetPath) {
        this.dtTargetPath = dtTargetPath;
    }

    public String getDtTitle() {
        return dtTitle;
    }

    public void setDtTitle(String dtTitle) {
        this.dtTitle = dtTitle;
    }
}
