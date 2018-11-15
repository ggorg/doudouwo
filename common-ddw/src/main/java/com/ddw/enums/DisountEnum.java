package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 *
 */
public enum DisountEnum implements Dictionary {
    //买进：0，赠送获取：1，已用：2
    value0("1折",10),
    value1("2折",20),
    value2("3折",30),
    value3("4折",40),
    value4("5折",50),
    value5("5.5折",55),
    value6("6折",60),
    value7("6.5折",65),
    value8("6.8折",68),
    value9("7折",70),
    value10("7.5折",75),
    value11("7.8折",78),
    value12("8折",80),
    value13("8.5折",85),
    value14("8.8折",88),
    value15("9折",90),
    value16("9.5折",95),
    value17("9.6折",96),
    value18("9.7折",97),
    value19("9.8折",98),
    value20("9.9折",99);





    private String name;
    private Integer code;

    private DisountEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DisountEnum c : DisountEnum.values()) {
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
