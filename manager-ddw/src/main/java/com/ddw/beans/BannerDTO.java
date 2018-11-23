package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Jacky on 2018/6/14.
 */
public class BannerDTO {
    private Integer id;
    private String name;
    private String url;
    private String bDescribe;
    private MultipartFile file;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getbDescribe() {
        return bDescribe;
    }

    public void setbDescribe(String bDescribe) {
        this.bDescribe = bDescribe;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
