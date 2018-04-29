package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 女神标识
 */
public enum BusinessCodeRuleEnum implements Dictionary {
    liveRadioCode("yyyyMMddHHmmssS01{storeid}{userid}{random}",0),
    goddessFlag1("女神",1);


    private String name;
    private Integer code;

    private BusinessCodeRuleEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (BusinessCodeRuleEnum c : BusinessCodeRuleEnum.values()) {
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
