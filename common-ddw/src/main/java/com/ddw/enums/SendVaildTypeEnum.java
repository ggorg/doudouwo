package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 状态
 */
public enum SendVaildTypeEnum implements Dictionary {
    typ1("实名验证",1),
    typ2("找回支付密码",2),
    typ3("绑定手机号",3),
    typ4("验证码",4);


    private String name;
    private Integer code;

    private SendVaildTypeEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (SendVaildTypeEnum c : SendVaildTypeEnum.values()) {
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
