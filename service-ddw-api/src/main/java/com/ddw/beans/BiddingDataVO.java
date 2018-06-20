package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiddingDataVO<T> implements Serializable {

    private static final long serialVersionUID = -213132036120371130L;
    @ApiModelProperty(name="bidEndTime",value="竞价结束时间",example="yyyy-MM-dd HH:mm:ss")
    private String bidEndTime;

    public String getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(String bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    @ApiModelProperty(name = "list", value = "数据", example = "")
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
