package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 订单类型
 */
public enum OrderTypeEnum  implements Dictionary {
    //商品:1,原材料：2
    OrderType1("商品",1),
    OrderType2("原材料",2),
    OrderType3("充值",3),
    OrderType4("竞价定金",4),
    OrderType5("竞价金额",5),
    OrderType6("女神-礼物",6),
    OrderType7("门票",7),
    OrderType8("逗币充值",8),
    OrderType9("竞价续费",9),
    OrderType10("代练支付",10);


    private String name;
    private Integer code;

    OrderTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (OrderTypeEnum c : OrderTypeEnum.values()) {
            if (c.getCode() == code || c.getCode().equals(code)) {
                return c.name;
            }
        }
        return null;
    }    // 普通方法
    public static OrderTypeEnum getOrderTypeEnum(Integer code) {
        for (OrderTypeEnum c : OrderTypeEnum.values()) {
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
