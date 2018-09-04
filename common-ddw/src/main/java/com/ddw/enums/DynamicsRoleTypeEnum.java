package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 动态角色类型
 */
public enum DynamicsRoleTypeEnum implements Dictionary {
    RoleType1("女神",1),
    RoleType2("代练",2);


    private String name;
    private Integer code;

    private DynamicsRoleTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DynamicsRoleTypeEnum c : DynamicsRoleTypeEnum.values()) {
            if (c.getCode() == code || c.getCode().equals(code)) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
