package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveReviewStatusVO<T> {


    @ApiModelProperty(name="liveRadioFlag",value="直播审核标记，0未申请，1审核通过,2审核中,3拒绝",example="直播审核标记，0未申请，1审核通过,2审核中,3拒绝")
    private Integer liveRadioFlag;

    @ApiModelProperty(name="liveRadioFlagStr",value="",example="")
    private String liveRadioFlagStr;

    public String getLiveRadioFlagStr() {
        return liveRadioFlagStr;
    }

    public void setLiveRadioFlagStr(String liveRadioFlagStr) {
        this.liveRadioFlagStr = liveRadioFlagStr;
    }

    public Integer getLiveRadioFlag() {
        return liveRadioFlag;
    }

    public void setLiveRadioFlag(Integer liveRadioFlag) {
        this.liveRadioFlag = liveRadioFlag;
    }
}
