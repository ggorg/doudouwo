package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 *
 */
public enum DoubiValueEnum implements Dictionary {
    value000("0.1元",10),
    value00("1元",100),
    value0("5元",500),
    value1("10元",1000),
    value2("20元",2000),
    value3("50元",5000),
    value4("100元",10000),
    value5("200元",20000),
    value6("300元",30000),
    value7("500元",50000),
    value8("1000元",100000);
    //买进：0，赠送获取：1，已用：2
    /*value0("你好美(1.50元)",150),
    value1("你很可爱(1.88元)",188),
    value2("你很可爱(1.88元)",188),
    value3("你很大方(5元)",500),
    value4("你很善良(6元)",600),
    value5("做我朋友(22.2元)",2220),
    value6("我想你了(33.3元)",3330),
    value7("我爱你(52.0元)",5200),
    value8("我喜欢你(52.1元)",5210),
    value9("我想在乎你一辈子(66.66元)",6666),
    value10("我想和你处对像(86.6元)",8660),
    value11("你很迷人(99.9元)",9990),
    value12("想和你认真在一起然后结婚(131.4元)",13140),
    value13("用命去爱你(199.99元)",19999);*/




    private String name;
    private Integer code;

    private DoubiValueEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (DoubiValueEnum c : DoubiValueEnum.values()) {
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
