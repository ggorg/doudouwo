package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/10/29.
 */
@ApiModel
public class OldBringingNewDTO {
    @ApiModelProperty(name="oldOpenid",value="老用户的标识,实际为UnionID",example="oNSHajg7OZ-K3yqzERRHOzudEm26102")
    private String oldOpenid;
    @ApiModelProperty(name="newOpenid",value="新用户的标识,实际为UnionID",example="oNSHajg7OZ-K3yqzERRHOzudEm27123")
    private String newOpenid;

    public String getOldOpenid() {
        return oldOpenid;
    }

    public void setOldOpenid(String oldOpenid) {
        this.oldOpenid = oldOpenid;
    }

    public String getNewOpenid() {
        return newOpenid;
    }

    public void setNewOpenid(String newOpenid) {
        this.newOpenid = newOpenid;
    }
}
