package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 *
 */
public enum RechargeValueEnum implements Dictionary {
    //买进：0，赠送获取：1，已用：2
    value0("5元",500),
    value1("10元",1000),
    value2("20元",2000),
    value3("50元",5000),
    value4("100元",10000),
    value5("200元",20000),
    value6("300元",30000),
    value7("500元",50000),
    value8("1000元",100000);





    private String name;
    private Integer code;

    private RechargeValueEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (RechargeValueEnum c : RechargeValueEnum.values()) {
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
