package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 直播房间状态
 */
public enum LiveStatusEnum implements Dictionary {
    liveStatus0("等待直播",0),
    liveStatus1("正在直播",1),
    liveStatus2("停用",2);


    private String name;
    private Integer code;

    private LiveStatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (LiveStatusEnum c : LiveStatusEnum.values()) {
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
