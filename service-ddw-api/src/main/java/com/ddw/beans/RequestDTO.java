package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel
public class RequestDTO<T> {
   /* @ApiModelProperty(name="token",value="令牌",example="xxxxxxxxxxxxx")
    @NotNull(message="token为空")
    @Pattern(regexp="^([0-9]{8}-[0-9]{4}-[0-9]{4}-[0-9]+)$",message = "token参数异常")
    private String token;*/

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

/*
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "token='" + token + '\'' +
                ", data=" + data +
                '}';
    }*/
}
