package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;

public class TicketDTO {
    private Integer id;
    private String dtName;
    private Integer dtPrice;
    private Integer dtActPrice;
    private Integer dtType;
    private String dtActiveTime;
    private String dtDesc;
    private MultipartFile dtImgFile;


    public MultipartFile getDtImgFile() {
        return dtImgFile;
    }

    public void setDtImgFile(MultipartFile dtImgFile) {
        this.dtImgFile = dtImgFile;
    }


    public String getDtDesc() {
        return dtDesc;
    }

    public void setDtDesc(String dtDesc) {
        this.dtDesc = dtDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDtName() {
        return dtName;
    }

    public void setDtName(String dtName) {
        this.dtName = dtName;
    }

    public Integer getDtPrice() {
        return dtPrice;
    }

    public void setDtPrice(Integer dtPrice) {
        this.dtPrice = dtPrice;
    }

    public Integer getDtActPrice() {
        return dtActPrice;
    }

    public void setDtActPrice(Integer dtActPrice) {
        this.dtActPrice = dtActPrice;
    }

    public Integer getDtType() {
        return dtType;
    }

    public void setDtType(Integer dtType) {
        this.dtType = dtType;
    }

    public String getDtActiveTime() {
        return dtActiveTime;
    }

    public void setDtActiveTime(String dtActiveTime) {
        this.dtActiveTime = dtActiveTime;
    }
}
