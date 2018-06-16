package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * Created by Jacky on 2018/6/16.
 */
public enum ButtonTypeEnum implements Dictionary{
    /**
     * 0URL跳转,1女神直播,2今日餐点,3大神代表,4好友约战,5预约房间,6pc上机,7霸屏上墙,8车队上分,9游戏竞猜
     */
    buttonType0("URL跳转",0),
    buttonType1("女神直播",1),
    buttonType2("今日餐点",2),
    buttonType3("大神代表",3),
    buttonType4("好友约战",4),
    buttonType5("预约房间",5),
    buttonType6("pc上机",6),
    buttonType7("霸屏上墙",7),
    buttonType8("车队上分",8),
    buttonType9("游戏竞猜",9);

    private String name;
    private Integer code;

    private ButtonTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ButtonTypeEnum c : ButtonTypeEnum.values()) {
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
