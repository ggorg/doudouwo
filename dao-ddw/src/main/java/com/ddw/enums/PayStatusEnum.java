package com.ddw.enums;

/**
 * 付款状态
 */
public enum PayStatusEnum {
    PayStatus0("未付款",0),
    PayStatus1("已付款",1),
    PayStatus2("退款",2);

    private String name;
    private Integer code;

    private PayStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (PayStatusEnum c : PayStatusEnum.values()) {
            if (c.getCode() == code) {
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
