package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

public class ActivityDTO {
    private Integer id;
    private String dtTitle;
    private String dtContent;
    private String dtTargetPath;
    private String dtDesc;

    /**
     * 引用连接的：1，本地地址的：2，使用文本内容的：3
     */
    private Integer dtType;
    private String activeTime;
    private MultipartFile dtImgFile;
    private MultipartFile zipFile;

    public String getDtDesc() {
        return dtDesc;
    }

    public void setDtDesc(String dtDesc) {
        this.dtDesc = dtDesc;
    }

    public MultipartFile getZipFile() {
        return zipFile;
    }

    public void setZipFile(MultipartFile zipFile) {
        this.zipFile = zipFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDtTitle() {
        return dtTitle;
    }

    public void setDtTitle(String dtTitle) {
        this.dtTitle = dtTitle;
    }

    public String getDtContent() {
        return dtContent;
    }

    public void setDtContent(String dtContent) {
        this.dtContent = dtContent;
    }

    public String getDtTargetPath() {
        return dtTargetPath;
    }

    public void setDtTargetPath(String dtTargetPath) {
        this.dtTargetPath = dtTargetPath;
    }

    public Integer getDtType() {
        return dtType;
    }

    public void setDtType(Integer dtType) {
        this.dtType = dtType;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public MultipartFile getDtImgFile() {
        return dtImgFile;
    }

    public void setDtImgFile(MultipartFile dtImgFile) {
        this.dtImgFile = dtImgFile;
    }
}
