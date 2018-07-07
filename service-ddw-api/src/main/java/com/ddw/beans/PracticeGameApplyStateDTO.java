package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/5.
 */
public class PracticeGameApplyStateDTO {
    @ApiModelProperty(name = "practieId",value = "代练ID，关联ddw_practice表",example = "8")
    private int practieId;

    public int getPractieId() {
        return practieId;
    }

    public void setPractieId(int practieId) {
        this.practieId = practieId;
    }
}
