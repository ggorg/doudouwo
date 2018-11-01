package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayTableDTO<T> {




    @ApiModelProperty(name = "tableCode", value = "桌号code，不传默认是建房间的桌号", example = "桌号code，不传默认是建房间的桌号")
    private Integer tableCode;

    @ApiModelProperty(name = "peopleMaxNum", value = "人数上限，不传默认是建房间的人数", example = "人数上限，不传默认是建房间的人数")
    private Integer peopleMaxNum;

    public Integer getTableCode() {
        return tableCode;
    }

    public void setTableCode(Integer tableCode) {
        this.tableCode = tableCode;
    }

    public Integer getPeopleMaxNum() {
        return peopleMaxNum;
    }

    public void setPeopleMaxNum(Integer peopleMaxNum) {
        this.peopleMaxNum = peopleMaxNum;
    }
}

