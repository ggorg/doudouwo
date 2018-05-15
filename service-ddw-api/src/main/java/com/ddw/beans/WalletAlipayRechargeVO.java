package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class WalletAlipayRechargeVO implements Serializable {

    @ApiModelProperty(name="app_id",value="支付宝分配给开发者的应用ID",example="2014072300007148")
    private String app_id;

    @ApiModelProperty(name="method",value="接口名称",example="koubei.trade.ticket.ticketcode.send")
    private String method;
    @ApiModelProperty(name="charset",value="请求使用的编码格式，如utf-8,gbk,gb2312等",example="wx8888888888888888")
    private String charset;

    @ApiModelProperty(name="sign_type",value="加密算法",example="RSA2")
    private String sign_type;

    @ApiModelProperty(name="sign",value="签名",example="xxxxxx")
    private String sign;

    @ApiModelProperty(name="timestamp",value="发送请求的时间",example="yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    @ApiModelProperty(name="version",value="接口版本",example="1.0")
    private String version;

    @ApiModelProperty(name="notify_url",value="回调地接",example="http://xxxxxxxxxxxxx")
    private String notify_url;

    @ApiModelProperty(name="biz_content",value="业务请求参数的集合",example="{xxxxxxxxx}")
    private String biz_content;

    @ApiModelProperty(name="orderNo",value="订单号",example="1111111111111111111")
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getBiz_content() {
        return biz_content;
    }

    public void setBiz_content(String biz_content) {
        this.biz_content = biz_content;
    }


}
