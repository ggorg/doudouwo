package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class GroupIdDTO {
    @ApiModelProperty(name="groupId",value="群组ID",example="")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupIdDTO{" +
                "groupId='" + groupId + '\'' +
                '}';
    }
}
