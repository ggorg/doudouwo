package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 提现状态
 */
public enum WithdrawStatusEnum implements Dictionary {
    withdrawStatus0("未提现",0),
    withdrawStatus1("已提现",1);



    private String name;
    private Integer code;

    private WithdrawStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (WithdrawStatusEnum c : WithdrawStatusEnum.values()) {
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
