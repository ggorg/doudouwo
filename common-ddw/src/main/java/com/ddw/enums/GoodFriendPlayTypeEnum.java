package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 类型
 */
public enum GoodFriendPlayTypeEnum implements Dictionary {
    type1("王者荣耀",1),
    type2("刺激战场",2),
    type3("全军出击",3),
    type4("炉石传说",4),
    type5("狼人杀",5),
    type100("其它",100);

            private String name;
            private Integer code;

    private GoodFriendPlayTypeEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
        public static String getName(Integer code) {
            for (GoodFriendPlayTypeEnum c : GoodFriendPlayTypeEnum.values()) {
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
