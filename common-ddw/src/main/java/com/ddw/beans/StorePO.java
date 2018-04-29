package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;

public class StorePO {
    private Integer id;
    private String dsName;
    private String dsLeaderName;
    private String dsLeaderTelNo;
    private String dsDesc;
    private String dsAddress;
    private String dsLongitude;
    private String dsLatitude;
    private String dsCity;
    private String dsCompanyName;
    private String dsBusinessCodePath;
    private Integer dsSort;
    private String dsCode;
    private String dsBankCardCode;
    private Date createTime;
    private Date updateTime;
    private Integer dsStatus;
    private String dsHeadUrl;

    public String getDsHeadUrl() {
        return dsHeadUrl;
    }

    public void setDsHeadUrl(String dsHeadUrl) {
        this.dsHeadUrl = dsHeadUrl;
    }

    public String getDsLongitude() {
        return dsLongitude;
    }

    public void setDsLongitude(String dsLongitude) {
        this.dsLongitude = dsLongitude;
    }

    public String getDsLatitude() {
        return dsLatitude;
    }

    public void setDsLatitude(String dsLatitude) {
        this.dsLatitude = dsLatitude;
    }

    public String getDsBusinessCodePath() {
        return dsBusinessCodePath;
    }

    public void setDsBusinessCodePath(String dsBusinessCodePath) {
        this.dsBusinessCodePath = dsBusinessCodePath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDsStatus() {
        return dsStatus;
    }

    public void setDsStatus(Integer dsStatus) {
        this.dsStatus = dsStatus;
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


}
