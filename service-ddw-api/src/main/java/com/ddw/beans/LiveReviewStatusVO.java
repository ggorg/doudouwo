package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveReviewStatusVO<T> {


    @ApiModelProperty(name="liveRadioFlag",value="直播审核标记，0未申请，1审核通过,2审核中,3拒绝",example="直播审核标记，0未申请，1审核通过,2审核中,3拒绝")
    private Integer liveRadioFlag=0;
    @ApiModelProperty(name="goddessFlag",value="女神标记，0非女神，1女神,2审核中,3拒绝",example="1")
    private Integer goddessFlag=0;
    @ApiModelProperty(name="realnameFlag",value="实名认证标记，0未实名，1已认证,2审核中,3拒绝",example="1")
    private Integer realnameFlag=0;

    public Integer getGoddessFlag() {
        return goddessFlag;
    }

    public void setGoddessFlag(Integer goddessFlag) {
        this.goddessFlag = goddessFlag;
    }

    public Integer getRealnameFlag() {
        return realnameFlag;
    }

    public void setRealnameFlag(Integer realnameFlag) {
        this.realnameFlag = realnameFlag;
    }

    public Integer getLiveRadioFlag() {
        return liveRadioFlag;
    }

    public void setLiveRadioFlag(Integer liveRadioFlag) {
        this.liveRadioFlag = liveRadioFlag;
    }

    @Override
    public String toString() {
        return "LiveReviewStatusVO{" +
                "liveRadioFlag=" + liveRadioFlag +
                ", goddessFlag=" + goddessFlag +
                ", realnameFlag=" + realnameFlag +
                '}';
    }
}
