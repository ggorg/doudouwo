package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class ResponseApiVO<T> {

    @ApiModelProperty(name="reCode",value="响应代码，负数都是失败，正数都是成功",example="1")
    private Integer reCode;
    @ApiModelProperty(name="reMsg",value="响应描述",example="成功")
    private String reMsg;

    @ApiModelProperty(name="data",value="业务数据",example="")
    private T data;
    public ResponseApiVO(Integer reCode, String reMsg, T data) {
        this.reCode = reCode;
        this.reMsg = reMsg;
        this.data = data;
    }


    public ResponseApiVO() {
    }
    public ResponseApiVO(ResponseVO<T> responseVO) {
        this.reCode=responseVO.getReCode();
        this.reMsg=responseVO.getReMsg();
        this.data=responseVO.getData();
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
        return "ResponseApiVO{" +
                "reCode=" + reCode +
                ", reMsg='" + reMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
