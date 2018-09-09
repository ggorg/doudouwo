package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class GoddessEvaluationDetailListDTO {
    @ApiModelProperty(name = "goddessId",value = "女神编号",example = "1")
    private Integer goddessId;
    @ApiModelProperty(name = "pageNo",value = "分页",example = "")
    private Integer pageNo;

    public Integer getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(Integer goddessId) {
        this.goddessId = goddessId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
