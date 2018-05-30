package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 商品状态
 */
public enum GoodsStatusEnum implements Dictionary {
    goodsStatus0("未上架",0),
    goodsStatus1("上架",1),
    goodsStatus2("已下架",2);

            private String name;
            private Integer code;

    private GoodsStatusEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
        public static String getName(Integer code) {
            for (GoodsStatusEnum c : GoodsStatusEnum.values()) {
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
