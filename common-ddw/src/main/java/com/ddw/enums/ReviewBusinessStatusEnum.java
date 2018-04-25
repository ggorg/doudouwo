package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批业务状态
 */
public enum ReviewBusinessStatusEnum implements Dictionary {
    //订单-取消订单：6，订单-退还：4
    ReviewBusinessStatus6("取消订单",6),
    ReviewBusinessStatus4("退还",4);
    private String name;
    private Integer code;

    private ReviewBusinessStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewBusinessStatusEnum c : ReviewBusinessStatusEnum.values()) {
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
