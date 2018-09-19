package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class StoreDTO {
    private Integer id;
    private String dsName;
    private String dsLeaderName;
    private String dsLeaderTelNo;
    private String dsDesc;
    private String dsAddress;
    private String dsLngLat;
    private String dsCity;
    private String dsCompanyName;
    private MultipartFile dsImgFile;
    private MultipartFile dsHeadFile;
    private MultipartFile dsBannerFile;
    private Integer dsSort;
    private String dsCode;
    private String dsBankCardCode;
    private ArrayList isUpdateImg;

    public MultipartFile getDsHeadFile() {
        return dsHeadFile;
    }

    public void setDsHeadFile(MultipartFile dsHeadFile) {
        this.dsHeadFile = dsHeadFile;
    }

    public MultipartFile getDsBannerFile() {
        return dsBannerFile;
    }

    public void setDsBannerFile(MultipartFile dsBannerFile) {
        this.dsBannerFile = dsBannerFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDsLeaderName() {
        return dsLeaderName;
    }

    public void setDsLeaderName(String dsLeaderName) {
        this.dsLeaderName = dsLeaderName;
    }

    public String getDsLeaderTelNo() {
        return dsLeaderTelNo;
    }

    public void setDsLeaderTelNo(String dsLeaderTelNo) {
        this.dsLeaderTelNo = dsLeaderTelNo;
    }

    public String getDsDesc() {
        return dsDesc;
    }

    public void setDsDesc(String dsDesc) {
        this.dsDesc = dsDesc;
    }

    public String getDsAddress() {
        return dsAddress;
    }

    public void setDsAddress(String dsAddress) {
        this.dsAddress = dsAddress;
    }

    public String getDsLngLat() {
        return dsLngLat;
    }

    public void setDsLngLat(String dsLngLat) {
        this.dsLngLat = dsLngLat;
    }

    public String getDsCity() {
        return dsCity;
    }

    public void setDsCity(String dsCity) {
        this.dsCity = dsCity;
    }

    public String getDsCompanyName() {
        return dsCompanyName;
    }

    public void setDsCompanyName(String dsCompanyName) {
        this.dsCompanyName = dsCompanyName;
    }

    public MultipartFile getDsImgFile() {
        return dsImgFile;
    }

    public void setDsImgFile(MultipartFile dsImgFile) {
        this.dsImgFile = dsImgFile;
    }

    public Integer getDsSort() {
        return dsSort;
    }

    public void setDsSort(Integer dsSort) {
        this.dsSort = dsSort;
    }

    public String getDsCode() {
        return dsCode;
    }

    public void setDsCode(String dsCode) {
        this.dsCode = dsCode;
    }

    public String getDsBankCardCode() {
        return dsBankCardCode;
    }

    public void setDsBankCardCode(String dsBankCardCode) {
        this.dsBankCardCode = dsBankCardCode;
    }

    public ArrayList getIsUpdateImg() {
        return isUpdateImg;
    }

    public void setIsUpdateImg(ArrayList isUpdateImg) {
        this.isUpdateImg = isUpdateImg;
    }
}
