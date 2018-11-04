package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 活动类型
 */
public enum ActivityTypeEnum implements Dictionary {
    type1("引用连接",1),
    type2("本地地址",2),
    type3("使用文本内容",3);

    private String name;
    private Integer code;

    private ActivityTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ActivityTypeEnum c : ActivityTypeEnum.values()) {
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
