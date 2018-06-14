package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批业务类型
 */
public enum ReviewBusinessTypeEnum implements Dictionary {
    //材料订单：1
    ReviewBusinessType1("材料订单",1),
    ReviewBusinessType2("会员",2),
    ReviewBusinessType3("直播",3),
    ReviewBusinessType4("身份认证",4),
    ReviewBusinessType5("女神",5),
    ReviewBusinessType6("代练",6),
    ReviewBusinessType7("banner申请",7);


    private String name;
    private Integer code;

    private ReviewBusinessTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewBusinessTypeEnum c : ReviewBusinessTypeEnum.values()) {
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
