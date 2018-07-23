package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 商品板块位置
 */
public enum GoodsPlatePosEnum implements Dictionary {

    GoodsPlatePos1("首页",1),
    GoodsPlatePos2("商城页",2);

    private String name;
    private Integer code;

    private GoodsPlatePosEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (GoodsPlatePosEnum c : GoodsPlatePosEnum.values()) {
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
