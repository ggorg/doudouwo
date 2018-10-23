package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 用户约玩开桌状态,是否参与
 */
public enum JoinOffLineStatusEnum implements Dictionary {
    Status0("否",0),
   Status1("是",1);

            private String name;
            private Integer code;

    private JoinOffLineStatusEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
        public static String getName(Integer code) {
            for (JoinOffLineStatusEnum c : JoinOffLineStatusEnum.values()) {
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
