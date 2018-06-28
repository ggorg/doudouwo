package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 *
 */
public enum DoubiRecordTypeEnum implements Dictionary {
    //买进：0，赠送获取：1，已用：2
    Type0("买进",0),
    Type1("赠送获取",1),
    Type2("已用",2);


    private String name;
    private Integer code;

    private DoubiRecordTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DoubiRecordTypeEnum c : DoubiRecordTypeEnum.values()) {
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
