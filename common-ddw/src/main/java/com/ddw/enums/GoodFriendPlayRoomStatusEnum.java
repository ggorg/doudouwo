package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 状态
 */
public enum GoodFriendPlayRoomStatusEnum implements Dictionary {
    status0("预约中",0),
    status1("约战中",1);


    private String name;
    private Integer code;

    private GoodFriendPlayRoomStatusEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (GoodFriendPlayRoomStatusEnum c : GoodFriendPlayRoomStatusEnum.values()) {
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
