package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批人类型
 */
public enum ReviewReviewerTypeEnum implements Dictionary {
    //审批人类型，总店：0，门店：1
    ReviewReviewerType0("总店",0),
    ReviewReviewerType1("门员",1);


    private String name;
    private Integer code;

    private ReviewReviewerTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewReviewerTypeEnum c : ReviewReviewerTypeEnum.values()) {
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
