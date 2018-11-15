package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayHistoryDTO<T> {




    @ApiModelProperty(name = "filterRoomId", value = "过滤房间ID，不传即不过滤", example = "过滤房间ID，不传即不过滤")
    private Integer filterRoomId;

    @ApiModelProperty(name = "pageNo", value = "页码", example = "页码")
    private Integer pageNo;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getFilterRoomId() {
        return filterRoomId;
    }

    public void setFilterRoomId(Integer filterRoomId) {
        this.filterRoomId = filterRoomId;
    }
}

