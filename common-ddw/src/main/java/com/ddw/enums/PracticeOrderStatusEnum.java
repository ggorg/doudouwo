package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * Created by Jacky on 2018/9/10.
 */
public enum PracticeOrderStatusEnum implements Dictionary {
    //订单状态，1开始接单，2完成,3代练结算未完成目标（双倍退差价），4用户提前结算1小时内扣违约金（总金额的30%），
    // 5用户提前结算超过1小时不扣违约金，6退款申请，6退款成功，8退款拒绝
    orderStatus1("开始接单",1),
    orderStatus2("完成",2),
    orderStatus3("代练结算未完成目标（双倍退差价）",3),
    orderStatus4("用户提前结算1小时内扣违约金（总金额的30%）",4),
    orderStatus5("用户提前结算超过1小时不扣违约金",5),
    orderStatus6("退款申请",6),
    orderStatus7("退款成功",7),
    orderStatus8("退款拒绝",8);

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
