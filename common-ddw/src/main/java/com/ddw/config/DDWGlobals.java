package com.ddw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DDWGlobals {

    @Value("${im.tls.sigcheck.lib.path}")
    private String libpath;

    @Value("${im.tls.sigcheck.lib.privatekey.path}")
    private String privateKeypath;

    @Value("${callbackurl.host:}")
    private String callBackHost;

    public String getCallBackHost() {
        return callBackHost;
    }

    public void setCallBackHost(String callBackHost) {
        this.callBackHost = callBackHost;
    }

    public String getPrivateKeypath() {
        return privateKeypath;
    }

    public void setPrivateKeypath(String privateKeypath) {
        this.privateKeypath = privateKeypath;
    }

    public String getLibpath() {
        return libpath;
    }

    public void setLibpath(String libpath) {
        this.libpath = libpath;
    }


}
