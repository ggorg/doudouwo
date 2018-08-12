package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批业务状态
 */
public enum ReviewBusinessStatusEnum implements Dictionary {
    //订单-取消订单：6，订单-退还：4
    orderStatus6("取消订单",6),
    orderStatus4("退还",4),
    liveRadio10("申请直播",10),
    realName11("实名认证申请",11),
    goddessFlag2("申请当女神",2),
    banner7("banner申请",7),
    practiceFlag5("申请代练",5),
    withdrawAppl8("提现申请",8),
    practiceRefund9("代练订单退款",9);
    private String name;
    private Integer code;

    ReviewBusinessStatusEnum(String name, Integer code) {
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
