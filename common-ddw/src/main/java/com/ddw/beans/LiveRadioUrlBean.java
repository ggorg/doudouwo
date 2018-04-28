package com.ddw.beans;

public class LiveRadioUrlBean {

    private String pushUrl;
    private String pullUrl;
    private String streamid;

    public String getStreamid() {
        return streamid;
    }

    public void setStreamid(String streamid) {
        this.streamid = streamid;
    }

    /**
     * 推流地址
     * @return
     */
    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    /**
     * 拉流地址
     * @return
     */
    public String getPullUrl() {
        return pullUrl;
    }

    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }
}
