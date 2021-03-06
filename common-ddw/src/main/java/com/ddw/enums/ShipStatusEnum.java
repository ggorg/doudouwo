package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 发货状态
 */
public enum ShipStatusEnum implements Dictionary {
    //发货状态，未发货：0，已接单：1，已发货：2，确认签收：3，退货：4，完成：5
    ShipStatus0("未发货",0),
    ShipStatus1("已接单",1),
    ShipStatus2("已发货",2),
    ShipStatus3("确认签收",3),
    ShipStatus4("退还",4),
    ShipStatus5("完成",5),
    ShipStatus6("关闭",6),
    ShipStatus7("退还-买家已发货",7),
    ShipStatus8("退还-卖家确认签收",8);

    private String name;
    private Integer code;

    private ShipStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ShipStatusEnum c : ShipStatusEnum.values()) {
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
