package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 优惠类型
 */
public enum CouponTypeEnum implements Dictionary {
    //优惠卷类型，代金卷：1，折扣卷：2，满减卷：3，首减卷：4
    CouponType1("代金卷",1),
    CouponType2("折扣卷",2),
    CouponType3("满减卷",3),
    CouponType4("首减卷",4);


    private String name;
    private Integer code;

    private CouponTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (CouponTypeEnum c : CouponTypeEnum.values()) {
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
