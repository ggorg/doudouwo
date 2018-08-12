package com.gen.common.enums;

import com.gen.common.dict.Dictionary;

/**
 * 用户类型
 */
public enum MessageUserTypeEnum implements Dictionary {

    userType1("用户",1),
    userType2("门店",2),
    userType3("总店",3);


    private String name;
    private Integer code;

    private MessageUserTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (MessageUserTypeEnum c : MessageUserTypeEnum.values()) {
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
