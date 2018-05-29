package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 商品类型
 */
public enum GoodsTypeEnum implements Dictionary {
    goodsType0("饮品",0),
    goodsType1("轻食",1),
    goodsType2("主食",2),
    goodsType3("套餐",3);

    private String name;
    private Integer code;

    private GoodsTypeEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (GoodsTypeEnum c : GoodsTypeEnum.values()) {
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
