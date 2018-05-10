package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectLiveRadioVO {

    @ApiModelProperty(name="pullUrl",value="播放地址",example="rtmp://xxxx")
    public String pullUrl;

    @ApiModelProperty(name="groupId",value="聊天群组",example="xxxxxxg")
    public String groupId;

    public String getPullUrl() {
        return pullUrl;
    }

    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
