package com.ddw.enums;

import com.gen.common.dict.Dictionary;

/**
 * 错误代码枚举
 */
public enum ErrorCodeEnum {

    commonOk1("成功",1),
    commonFail1("系统异常",-1),
    commonFail2("失败",-2),
    applyLiveRadioFail2000("用户不存在",-2000),
    applyLiveRadioFail2001("请先申请当女神",-2001),
    applyLiveRadioFail2002("直播房间已开，请关闭再申请",-2002),
    applyLiveRadioFail2003("正在审核中，请耐心等待",-2003);


    private  String name;
    private  Integer code;

    private  ErrorCodeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (ErrorCodeEnum c : ErrorCodeEnum.values()) {
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

    public  Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
