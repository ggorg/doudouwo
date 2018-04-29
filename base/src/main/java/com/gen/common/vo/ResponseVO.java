package com.gen.common.vo;

public class ResponseVO<T> {
    private Integer reCode;
    private String reMsg;

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
