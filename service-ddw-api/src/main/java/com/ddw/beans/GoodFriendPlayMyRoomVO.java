package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayMyRoomVO<T> {


    @ApiModelProperty(name = "roomCode", value = "roomCode", example = "roomCode")
    private Integer roomCode;

    public Integer getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(Integer roomCode) {
        this.roomCode = roomCode;
    }
}

