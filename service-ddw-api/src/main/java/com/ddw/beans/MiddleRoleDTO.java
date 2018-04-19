package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色中间表
 * Created by Jacky on 2018/4/16.
 */
@ApiModel(value="角色中建表用例对象",description="用例对象MiddleRoleDTO")
public class MiddleRoleDTO {
    @ApiModelProperty(name="userId",value="用户表关联ID",example="1")
    private int userId;
    @ApiModelProperty(name="roleName",value="角色名称（代练、女神，以及后续扩展）",example="女神")
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "MiddleRoleDTO{" +
                "roleName='" + roleName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
