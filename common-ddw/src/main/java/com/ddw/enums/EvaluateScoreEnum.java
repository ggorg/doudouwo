package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 评价分数
 */
public enum EvaluateScoreEnum implements Dictionary {
    Evaluate1("1分",1),
    Evaluate2("2分",2),
    Evaluate3("3分",3),
    Evaluate4("4分",4),
    Evaluate5("5分",5);


    private String name;
    private Integer code;

    private EvaluateScoreEnum(String name, Integer code) {
                this.name = name;
                this.code = code;
            }

            // 普通方法
    public static String getName(Integer code) {
            for (EvaluateScoreEnum c : EvaluateScoreEnum.values()) {
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
