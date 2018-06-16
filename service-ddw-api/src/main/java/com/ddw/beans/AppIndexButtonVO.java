package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 菜单按钮
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexButtonVO implements Serializable {
    @ApiModelProperty(name="name",value="名称",example="女神直播")
    private String name;
    @ApiModelProperty(name="url",value="跳转URL",example="http://doudouwo.cn/")
    private String url;
    @ApiModelProperty(name="type",value="类型,0URL跳转,1女神直播,2今日餐点,3大神代表,4好友约战,5预约房间,6pc上机,7霸屏上墙,8车队上分,9游戏竞猜",example="1")
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
