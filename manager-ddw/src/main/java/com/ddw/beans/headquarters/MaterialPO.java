package com.ddw.beans.headquarters;

import java.util.Date;

public class MaterialPO {
    private Integer id;
    private String dmName;
    private String dmUnit;
    private Integer dmCost;
    private Integer dmSales;
    private Date createTime;
    private Date updateTime;
    private String dmContent;

    private String dmCode;
    private String dmImgPath;
    private String dmIcoImgPath;

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
