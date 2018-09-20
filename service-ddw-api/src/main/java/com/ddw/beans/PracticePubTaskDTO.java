package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/24.
 */
public class PracticePubTaskDTO {
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private Integer practiceId;
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    public Integer getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Integer practiceId) {
        this.practiceId = practiceId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
