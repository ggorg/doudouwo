package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 商品页面发布状态
 */
public enum GoodsPageModelStatusEnum implements Dictionary {
    goodsStatus0("未发布",0),
    goodsStatus1("已发布",1);

            private String name;
            private Integer code;

    private GoodsPageModelStatusEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
        public static String getName(Integer code) {
            for (GoodsPageModelStatusEnum c : GoodsPageModelStatusEnum.values()) {
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
