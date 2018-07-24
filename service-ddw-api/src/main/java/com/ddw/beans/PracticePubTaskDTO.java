package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/24.
 */
public class PracticePubTaskDTO {
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private Integer practiceId;
    private PageDTO page;

    public Integer getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Integer practiceId) {
        this.practiceId = practiceId;
    }

    public PageDTO getPage() {
        return page;
    }

    public void setPage(PageDTO page) {
        this.page = page;
    }
}
