package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 发货状态
 */
public enum ClientShipStatusEnum implements Dictionary {
    //发货状态，未发货：0，已接单：1，已发货：2，确认签收：3，退货：4，完成：5
    ShipStatus0("未接单",0),
    ShipStatus1("已接单",1),
    ShipStatus2("已发货",2),
    ShipStatus4("退订",4),
    ShipStatus5("完成",5);


    private String name;
    private Integer code;

    private ClientShipStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ClientShipStatusEnum c : ClientShipStatusEnum.values()) {
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
