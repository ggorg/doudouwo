package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 推流状态
 */
public enum LiveEventTypeEnum implements Dictionary {
    eventType0("断流",0),
    eventType1("推流",1);


    private String name;
    private Integer code;

    private LiveEventTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (LiveEventTypeEnum c : LiveEventTypeEnum.values()) {
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
