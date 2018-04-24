package com.ddw.beans;

import java.util.Date;

/**
 * 原材料持久化参数
 */
public class MaterialPO {
    private Integer id;
    private String dmName;
    private String dmUnit;
    private Integer dmNetWeight;
    private Integer dmCost;
    private Integer dmSales;
    private Date createTime;
    private Date updateTime;
    private String dmContent;

    private String dmCode;
    private String dmImgPath;
    private String dmIcoImgPath;
    private Integer dmCurrentCount;
    private Integer dmVersion;
    private Integer dmStatus;
    private Integer dmSort;

    public Integer getDmNetWeight() {
        return dmNetWeight;
    }

    public void setDmNetWeight(Integer dmNetWeight) {
        this.dmNetWeight = dmNetWeight;
    }

    public Integer getDmSort() {
        return dmSort;
    }

    public void setDmSort(Integer dmSort) {
        this.dmSort = dmSort;
    }

    public Integer getDmStatus() {
        return dmStatus;
    }

    public void setDmStatus(Integer dmStatus) {
        this.dmStatus = dmStatus;
    }

    public Integer getDmVersion() {
        return dmVersion;
    }

    public void setDmVersion(Integer dmVersion) {
        this.dmVersion = dmVersion;
    }

    public Integer getDmCurrentCount() {
        return dmCurrentCount;
    }

    public void setDmCurrentCount(Integer dmCurrentCount) {
        this.dmCurrentCount = dmCurrentCount;
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

    public String getDmImgPath() {
        return dmImgPath;
    }

    public void setDmImgPath(String dmImgPath) {
        this.dmImgPath = dmImgPath;
    }

    public String getDmIcoImgPath() {
        return dmIcoImgPath;
    }

    public void setDmIcoImgPath(String dmIcoImgPath) {
        this.dmIcoImgPath = dmIcoImgPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDmName() {
        return dmName;
    }

    public void setDmName(String dmName) {
        this.dmName = dmName;
    }

    public String getDmUnit() {
        return dmUnit;
    }

    public void setDmUnit(String dmUnit) {
        this.dmUnit = dmUnit;
    }

    public Integer getDmCost() {
        return dmCost;
    }

    public void setDmCost(Integer dmCost) {
        this.dmCost = dmCost;
    }

    public Integer getDmSales() {
        return dmSales;
    }

    public void setDmSales(Integer dmSales) {
        this.dmSales = dmSales;
    }

    public String getDmCode() {
        return dmCode;
    }

    public void setDmCode(String dmCode) {
        this.dmCode = dmCode;
    }


    public String getDmContent() {
        return dmContent;
    }

    public void setDmContent(String dmContent) {
        this.dmContent = dmContent;
    }


}
