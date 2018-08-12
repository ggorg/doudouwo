package com.gen.common.enums;

import com.gen.common.dict.Dictionary;

/**
 * 消息状态
 */
public enum MessageTypeEnum implements Dictionary {

    MessageType1("警报",1);


    private String name;
    private Integer code;

    private MessageTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (MessageTypeEnum c : MessageTypeEnum.values()) {
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
