package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class GiftDTO {
    private Integer id;
    private String dgName;
    private Integer dgPrice;
    private Integer dgActPrice;
    private MultipartFile dgImg;
    private Integer dgSort;
     private ArrayList isUpdateImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgName() {
        return dgName;
    }

    public void setDgName(String dgName) {
        this.dgName = dgName;
    }

    public Integer getDgPrice() {
        return dgPrice;
    }

    public void setDgPrice(Integer dgPrice) {
        this.dgPrice = dgPrice;
    }

    public Integer getDgActPrice() {
        return dgActPrice;
    }

    public void setDgActPrice(Integer dgActPrice) {
        this.dgActPrice = dgActPrice;
    }

    public MultipartFile getDgImg() {
        return dgImg;
    }

    public void setDgImg(MultipartFile dgImg) {
        this.dgImg = dgImg;
    }

    public ArrayList getIsUpdateImg() {
        return isUpdateImg;
    }

    public void setIsUpdateImg(ArrayList isUpdateImg) {
        this.isUpdateImg = isUpdateImg;
    }

    public Integer getDgSort() {
        return dgSort;
    }

    public void setDgSort(Integer dgSort) {
        this.dgSort = dgSort;
    }
}
