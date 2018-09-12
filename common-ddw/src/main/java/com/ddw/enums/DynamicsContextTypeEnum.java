package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 动态角色类型
 */
public enum DynamicsContextTypeEnum implements Dictionary {
    DynamicsContextType1("文字",1),
    DynamicsContextType2("图文",2);


    private String name;
    private Integer code;

    private DynamicsContextTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DynamicsContextTypeEnum c : DynamicsContextTypeEnum.values()) {
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
