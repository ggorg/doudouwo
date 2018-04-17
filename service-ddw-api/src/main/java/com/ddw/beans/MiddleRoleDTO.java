package com.ddw.beans;

/**
 * 角色中间表
 * Created by Jacky on 2018/4/16.
 */
public class MiddleRoleDTO {
    private int userId;
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

}
