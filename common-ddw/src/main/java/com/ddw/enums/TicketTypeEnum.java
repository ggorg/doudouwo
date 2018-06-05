package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 商品类型
 */
public enum TicketTypeEnum implements Dictionary {
    goodsType0("白天票",0),
    goodsType1("晚上票",1),
    goodsType2("通票",2),
    goodsType3("狼人杀",3);

    private String name;
    private Integer code;

    private TicketTypeEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (TicketTypeEnum c : TicketTypeEnum.values()) {
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
