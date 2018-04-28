package com.ddw.beans;

public class IMCallBackDTO {

    /**
     * APP在云通讯申请的Appid。
     */
    private String SdkAppid;
    private String CallbackCommand;

    /**
     * json
     */
    private String contenttype;

    /**
     * 客户端IP地址。

     */
    private String ClientIP;

    /**
     *
     客户端平台。对应不同的平台类型，可能的取值有：
     RESTAPI（使用REST API发送请求）、Web（使用Web SDK发送请求）、
     Android、iOS、Windows、Mac、Unkown（使用未知类型的设备发送请求）
     */
    private String OptPlatform;

    public String getSdkAppid() {
        return SdkAppid;
    }

    public void setSdkAppid(String sdkAppid) {
        SdkAppid = sdkAppid;
    }

    public String getCallbackCommand() {
        return CallbackCommand;
    }

    public void setCallbackCommand(String callbackCommand) {
        CallbackCommand = callbackCommand;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getClientIP() {
        return ClientIP;
    }

    public void setClientIP(String clientIP) {
        ClientIP = clientIP;
    }

    public String getOptPlatform() {
        return OptPlatform;
    }

    public void setOptPlatform(String optPlatform) {
        OptPlatform = optPlatform;
    }

    @Override
    public String toString() {
        return "IMCallBackDTO{" +
                "SdkAppid='" + SdkAppid + '\'' +
                ", CallbackCommand='" + CallbackCommand + '\'' +
                ", contenttype='" + contenttype + '\'' +
                ", ClientIP='" + ClientIP + '\'' +
                ", OptPlatform='" + OptPlatform + '\'' +
                '}';
    }
}
