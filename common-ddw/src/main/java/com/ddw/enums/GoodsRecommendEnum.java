package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 是否推荐
 */
public enum GoodsRecommendEnum implements Dictionary {
    goodsRecommend0("否",0),
    goodsRecommend1("是",1);

            private String name;
            private Integer code;

    private GoodsRecommendEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
        public static String getName(Integer code) {
            for (GoodsRecommendEnum c : GoodsRecommendEnum.values()) {
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
