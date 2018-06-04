package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 状态
 */
public enum DisabledEnum implements Dictionary {
    disabled0("启用",0),
    disabled1("停用",1);


    private String name;
    private Integer code;

    private DisabledEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (DisabledEnum c : DisabledEnum.values()) {
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
