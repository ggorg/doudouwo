package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayCreateRoomDTO<T> {



    @ApiModelProperty(name = "bgImg", value = "主题图", example = "主题图")
    private  MultipartFile bgImg;

    @ApiModelProperty(name = "name", value = "主题名称", example = "主题名称")
    private String name;
    @ApiModelProperty(name = "type", value = "主题类型", example = "主题类型")
    private Integer type;
    @ApiModelProperty(name = "tableCode", value = "桌号code", example = "桌号code")
    private Integer tableCode;

    @ApiModelProperty(name = "peopleMaxNum", value = "人数上限", example = "人数上限")
    private Integer peopleMaxNum;
    @ApiModelProperty(name = "endTime", value = "结束时间", example = "结束时间")
    private Date endTime;


    public MultipartFile getBgImg() {
        return bgImg;
    }

    public void setBgImg(MultipartFile bgImg) {
        this.bgImg = bgImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}

