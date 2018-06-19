package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 审批回调业务
 */
public enum ReviewCallBackEnum implements Dictionary {
    //订单-取消订单：6，订单-退还：4
    orderStatus6("取消订单",6),
    orderStatus4("退还",4),
    liveRadioCallBack10("executeLiveRadio",10),//申请直播
    realName11("executeRealName",11),//实名认证申请
    goddessFlagCallBack2("executeGoddess",2),//申请当女神
    bannerCallBack7("executeBanner",7),//banner申请
    practiceFlag5("executePractice",5);//申请代练
    private String name;
    private Integer code;

    private ReviewCallBackEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ReviewCallBackEnum c : ReviewCallBackEnum.values()) {
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
