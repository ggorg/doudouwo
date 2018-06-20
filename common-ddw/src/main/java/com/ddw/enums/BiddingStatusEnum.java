package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 竟价状态
 */
public enum BiddingStatusEnum implements Dictionary {
    Status1("竞价中",1),
    Status2("接单中",2),
    Status3("用户已支付",3),
    Status4("用户已取消支付",4),
    Status5("约玩中",5),
    Status6("空闲中",6),
    Status7("未开始",7),
    Status8("已结束",8);

    private String name;
    private Integer code;

    private BiddingStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (BiddingStatusEnum c : BiddingStatusEnum.values()) {
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
