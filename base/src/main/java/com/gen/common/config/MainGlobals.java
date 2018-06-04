package com.gen.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainGlobals {

    @Value("${gen.rs.dir:/data/rs}")
    private String rsDir;
    @Value("${swagger.serviceUrl:http://www.doudouwo.cn}")
    private String serviceUrl;


    public String getRsDir() {
        return rsDir;
    }

    public void setRsDir(String rsDir) {
        this.rsDir = rsDir;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
