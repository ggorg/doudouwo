package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 库存类型
 */
public enum DmiTypeEnum implements Dictionary {
    //库存动作,入库：0，出库：1
    DmiType0("入库",0),
    DmiType1("出库",1);


    private String name;
    private Integer code;

    private DmiTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DmiTypeEnum c : DmiTypeEnum.values()) {
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
