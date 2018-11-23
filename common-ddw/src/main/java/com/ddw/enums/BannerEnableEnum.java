package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * BannerEnableEnum
 */
public enum BannerEnableEnum implements Dictionary {
    type0("下架",0),
    type1("发布",1);

    private String name;
    private Integer code;

    private BannerEnableEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (BannerEnableEnum c : BannerEnableEnum.values()) {
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
