package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/8/8.
 */
@ApiModel(value="会员资料查询用例",description="UserQueryDTO")
public class UserQueryDTO {
    @ApiModelProperty(name="id",value="会员编号,非必填",example="1")
    private Integer id;
    @ApiModelProperty(name="openid",value="用户openid,非必填",example="oNSHajg7OZ-K3yqzERRHOzudEm26102")
    private String openid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
