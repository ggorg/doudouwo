package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeGameApplyStateDTO {
    @ApiModelProperty(name = "drBusinessCode",value = "所属业务编号或者ID，关联ddw_review表",example = "12345")
    private String drBusinessCode;

    public String getDrBusinessCode() {
        return drBusinessCode;
    }

    public void setDrBusinessCode(String drBusinessCode) {
        this.drBusinessCode = drBusinessCode;
    }
}
