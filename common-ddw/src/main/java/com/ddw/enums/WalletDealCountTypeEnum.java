package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 支付类型
 */
public enum WalletDealCountTypeEnum implements Dictionary {
    WalletDealCountType1("支出",1),
    WalletDealCountType2("收入",2);

    private String name;
    private Integer code;

    private WalletDealCountTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (WalletDealCountTypeEnum c : WalletDealCountTypeEnum.values()) {
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
