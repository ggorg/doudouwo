package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 女神
 */
@ApiModel(value="女神代码",description="女神代码")
public class GoddessCodeDTO {
    @ApiModelProperty(name="goddessCode",value="女神编号",example="女神编号")
    private Integer goddessCode;

    public Integer getGoddessCode() {
        return goddessCode;
    }

    public void setGoddessCode(Integer goddessCode) {
        this.goddessCode = goddessCode;
    }
}
