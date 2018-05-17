package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 女神预约开关
 */
public enum GoddessAppointmentEnum implements Dictionary {
    status0("关闭",0),
    status1("开启",1);

    private String name;
    private Integer code;

    private GoddessAppointmentEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (GoddessAppointmentEnum c : GoddessAppointmentEnum.values()) {
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
