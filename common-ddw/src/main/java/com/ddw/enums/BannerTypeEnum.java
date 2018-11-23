package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * banner类型
 */
public enum BannerTypeEnum implements Dictionary {
    type1("首页",1),
    type2("商品",2);

    private String name;
    private Integer code;

    private BannerTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (BannerTypeEnum c : BannerTypeEnum.values()) {
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
