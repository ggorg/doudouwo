package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 支付类型
 */
public enum PayTypeEnum implements Dictionary {
    PayType1("微信",1),
    PayType2("支付宝",2),
    PayType3("线下",3);

    private String name;
    private Integer code;

    private PayTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (PayTypeEnum c : PayTypeEnum.values()) {
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
