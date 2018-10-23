package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayOutUserDTO<T> {


    @ApiModelProperty(name = "code", value = "房间code", example = "房间code")
    private  Integer code;


    @ApiModelProperty(name = "openId", value = "openId", example = "openId")
    private  String openId;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}

