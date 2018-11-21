package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 收益类型
 */
public enum IncomeTypeEnum implements Dictionary {
    IncomeType1("女神收益",1),
    IncomeType2("代练收益",2);


    private String name;
    private Integer code;

    private IncomeTypeEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (IncomeTypeEnum c : IncomeTypeEnum.values()) {
                if (c.getCode() == code || c.getCode().equals(code)) {
                    return c.name;
                }
        }
        return null;
    }
    public static IncomeTypeEnum getIncomeTypeEnum(Integer code) {
        for (IncomeTypeEnum c : IncomeTypeEnum.values()) {
            if (c.getCode() == code || c.getCode().equals(code)) {
                return c;
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
