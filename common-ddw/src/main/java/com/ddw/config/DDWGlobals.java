package com.ddw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DDWGlobals {

    @Value("${im.tls.sigcheck.lib.path}")
    private String libpath;


    public String getLibpath() {
        return libpath;
    }

    public void setLibpath(String libpath) {
        this.libpath = libpath;
    }


}
