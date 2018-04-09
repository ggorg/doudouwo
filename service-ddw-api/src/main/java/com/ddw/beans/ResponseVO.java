package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ResponseVO<T> {

    @ApiModelProperty(name="reCode",value="响应代码，负数都是失败，正数都是成功",example="1")
    private Integer reCode;
    @ApiModelProperty(name="reMsg",value="响应描述",example="成功")
    private String reMsg;

    @ApiModelProperty(name="data",value="业务数据",example="")
    private T data;
    public ResponseVO(Integer reCode, String reMsg, T data) {
        this.reCode = reCode;
        this.reMsg = reMsg;
        this.data = data;
    }


    public ResponseVO() {
    }

    public Integer getReCode() {
        return reCode;
    }

    public void setReCode(Integer reCode) {
        this.reCode = reCode;
    }

    public String getReMsg() {
        return reMsg;
    }

    public void setReMsg(String reMsg) {
        this.reMsg = reMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "reCode=" + reCode +
                ", reMsg='" + reMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
