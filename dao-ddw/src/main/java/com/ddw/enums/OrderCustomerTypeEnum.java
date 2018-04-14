package com.ddw.enums;

/**
 * 订单用户类型
 */
public enum OrderCustomerTypeEnum {
    //用户类型 ,普通会员：0，女神会员：1，代练会员：2，服务员：3，门店：4
    OrderCustomerType0("普通会员",0),
    OrderCustomerType1("女神会员",1),
    OrderCustomerType2("代练会员",2),
    OrderCustomerType3("服务员",3),
    OrderCustomerType4("门店",4);

    private String name;
    private Integer code;

    private OrderCustomerTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (OrderCustomerTypeEnum c : OrderCustomerTypeEnum.values()) {
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
