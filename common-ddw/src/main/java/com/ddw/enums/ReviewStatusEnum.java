package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批状态
 */
public enum ReviewStatusEnum implements Dictionary {
    //待审核：0，审核通过：1，审核不通过：2
    ReviewStatus0("待审核",0),
    ReviewStatus1("审核通过",1),
    ReviewStatus2("审核不通过",2);


    private String name;
    private Integer code;

    private ReviewStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewStatusEnum c : ReviewStatusEnum.values()) {
            if (c.getCode() == code || c.getCode().equals(code)) {
                return c.name;
            }
        }
        return null;
    }
    // 普通方法
    public static ReviewStatusEnum get(Integer code) {
        for (ReviewStatusEnum c : ReviewStatusEnum.values()) {
            if (c.getCode() == code || c.getCode().equals(code)) {
                return c;
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
