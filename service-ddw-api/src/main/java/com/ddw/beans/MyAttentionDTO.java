package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/4/19.
 */
@ApiModel(value = "我的关注用例对象",description = "我的关注用例对象")
public class MyAttentionDTO {

    @ApiModelProperty(name = "goddessId",value = "女神ID，关联ddw_goddess表",example = "1")
    private int goddessId;

    @ApiModelProperty(name = "practiceId",value = "代练ID，关联ddw_practice表",example = "1")
    private int practiceId;

    public int getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(int goddessId) {
        this.goddessId = goddessId;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }
}
