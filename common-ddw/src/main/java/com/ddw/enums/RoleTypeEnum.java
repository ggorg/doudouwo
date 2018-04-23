package com.ddw.enums;

/**
 * 角色类型
 */
public enum RoleTypeEnum {
    RoleType1_0("总店","1_0"),
    RoleType1_1("普通","1_1"),
    RoleType1_3("所有","1_3");

    private String name;
    private String code;

    private RoleTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
