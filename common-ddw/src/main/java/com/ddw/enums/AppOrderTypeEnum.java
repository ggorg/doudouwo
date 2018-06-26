package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * app订单类型
 */
public enum AppOrderTypeEnum implements Dictionary {
    //商品:1,原材料：2
    OrderType1("(4,5,6)",1),
    OrderType2("(6)",2),
    OrderType3("(1,3,6,7)",3);



    private String name;
    private Integer code;

    private AppOrderTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (AppOrderTypeEnum c : AppOrderTypeEnum.values()) {
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
