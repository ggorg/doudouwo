package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderViewEvaluateListDTO {


    @ApiModelProperty(name="code",value="业务号",example="业务号")
    private Integer code;
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
