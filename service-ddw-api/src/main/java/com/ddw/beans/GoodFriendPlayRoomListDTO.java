package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayRoomListDTO<T> {



    @ApiModelProperty(name = "pageNo", value = "页码", example = "页码")
    private Integer pageNo;
    @ApiModelProperty(name = "type", value = "类型", example = "类型")
    private Integer type;

    @ApiModelProperty(name = "status", value = "预约中：0，约战中：1", example = "预约中：0，约战中：1")
    private Integer status;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

