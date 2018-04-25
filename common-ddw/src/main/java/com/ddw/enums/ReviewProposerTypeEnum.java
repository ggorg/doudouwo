package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 申请人类型
 */
public enum ReviewProposerTypeEnum implements Dictionary {
    //申请人类型，门店：0，会员：1
    ReviewProposerType0("门店",0),
    ReviewProposerType1("会员",1);


    private String name;
    private Integer code;

    private ReviewProposerTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewProposerTypeEnum c : ReviewProposerTypeEnum.values()) {
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
