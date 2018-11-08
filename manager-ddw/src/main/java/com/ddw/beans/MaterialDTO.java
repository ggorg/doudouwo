package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * 原材料输入参数
 */
public class MaterialDTO {
    private Integer id;
    private String dmName;
    private String dmUnit;
    private Integer dmCost;
    private Integer dmSales;
    private String dmCode;
    private MultipartFile dmImgFile;
    private String dmContent;
    private ArrayList isUpdateImg;
    private Integer dmSort;

    private Integer dmNetWeight;

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

    public ArrayList getIsUpdateImg() {
        return isUpdateImg;
    }

    public void setIsUpdateImg(ArrayList isUpdateImg) {
        this.isUpdateImg = isUpdateImg;
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

    public MultipartFile getDmImgFile() {
        return dmImgFile;
    }

    public void setDmImgFile(MultipartFile dmImgFile) {
        this.dmImgFile = dmImgFile;
    }

    public String getDmContent() {
        return dmContent;
    }

    public void setDmContent(String dmContent) {
        this.dmContent = dmContent;
    }

    @Override
    public String toString() {
        return "MaterialDTO{" +
                "id=" + id +
                ", dmName='" + dmName + '\'' +
                ", dmUnit='" + dmUnit + '\'' +
                ", dmCost=" + dmCost +
                ", dmSales=" + dmSales +
                ", dmCode='" + dmCode + '\'' +
                ", dmImgFile=" + dmImgFile +
                ", dmContent='" + dmContent + '\'' +
                '}';
    }
}
