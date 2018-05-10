package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletWeixinRechargeVO implements Serializable {
    @ApiModelProperty(name="appid",value="应用ID",example="wx8888888888888888")
    private String appid;

    @ApiModelProperty(name="partnerid",value="商户号",example="1900000109")
    private String partnerid;

    @ApiModelProperty(name="prepayid",value="预支付交易会话ID",example="WX1217752501201407033233368018")
    private String prepayid;

    @ApiModelProperty(name="packages",value="扩展字段,暂填写固定值Sign=WXPay",example="Sign=WXPay")
    @JsonProperty("package")
    private String packages;

    @ApiModelProperty(name="noncestr",value="随机字符串",example="5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
    private String noncestr;

    @ApiModelProperty(name="timestamp",value="时间戳",example="1412000000")
    private String timestamp;

    @ApiModelProperty(name="sign",value="签名",example="C380BEC2BFD727A4B6845133519F3AD6")
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
