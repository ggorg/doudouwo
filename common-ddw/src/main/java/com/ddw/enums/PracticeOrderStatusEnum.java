package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * Created by Jacky on 2018/9/10.
 */
public enum PracticeOrderStatusEnum implements Dictionary {
    orderStatus1("开始接单",1),
    orderStatus2("完成",2),
    orderStatus3("未完成并结单",3),
    orderStatus4("退款申请",4),
    orderStatus5("退款成功",5),
    orderStatus6("退款拒绝",6);

    private String name;
    private Integer code;

    PracticeOrderStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (PracticeOrderStatusEnum c : PracticeOrderStatusEnum.values()) {
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
