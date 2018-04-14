package com.ddw.enums;

/**
 * 发货状态
 */
public enum ShipStatusEnum {
    //发货状态，未发货：0，已接单：1，已发货：2，确认签收：3，退货：4，完成：5
    ShipStatus0("未发货",0),
    ShipStatus1("已接单",1),
    ShipStatus2("已发货",2),
    ShipStatus3("确认签收",3),
    ShipStatus4("退货",4),
    ShipStatus5("完成",5);

    private String name;
    private Integer code;

    private ShipStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ShipStatusEnum c : ShipStatusEnum.values()) {
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
