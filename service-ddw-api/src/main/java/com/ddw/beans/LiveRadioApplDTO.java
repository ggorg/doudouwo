package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LiveRadioApplDTO {
    @ApiModelProperty(name = "roomName", value = "房间名称", example = "")
    private String roomName;

    @ApiModelProperty(name="langlat",value="坐标",example="116.493956,39.960963")
    private String langlat;

    public String getLanglat() {
        return langlat;
    }

    public void setLanglat(String langlat) {
        this.langlat = langlat;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
