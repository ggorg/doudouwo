package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PracticeReviewStatusVO<T> {



    @ApiModelProperty(name="practiceFlag",value="代练标记，0非代练，1代练,2审核中,3拒绝",example="1")
    private Integer practiceFlag=0;
    @ApiModelProperty(name="realnameFlag",value="实名认证标记，0未实名，1已认证,2审核中,3拒绝",example="1")
    private Integer realnameFlag=0;

    public Integer getPracticeFlag() {
        return practiceFlag;
    }

    public void setPracticeFlag(Integer practiceFlag) {
        this.practiceFlag = practiceFlag;
    }

    public Integer getRealnameFlag() {
        return realnameFlag;
    }

    public void setRealnameFlag(Integer realnameFlag) {
        this.realnameFlag = realnameFlag;
    }
}
