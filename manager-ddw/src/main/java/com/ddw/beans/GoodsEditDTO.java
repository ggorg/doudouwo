package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class GoodsEditDTO {
    private Integer id;
    private String dgTitle;
    private Integer dgType;
    private Integer dgSort;
    private ArrayList isUpdateImg;
    private MultipartFile fileImgShow;
    private String dgDetail;
    private Integer dgRecommend;
    private Integer[] dghId;
    private String[] dghName;
    private Integer[] dghCost;
    private Integer[] dghSalesPrice;
    private Integer[] dghActivityPrice;
    private Integer[] dghFormulaId;
    private Integer[] dghStatus;

    public Integer getDgRecommend() {
        return dgRecommend;
    }

    public void setDgRecommend(Integer dgRecommend) {
        this.dgRecommend = dgRecommend;
    }

    public Integer[] getDghStatus() {
        return dghStatus;
    }

    public void setDghStatus(Integer[] dghStatus) {
        this.dghStatus = dghStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgTitle() {
        return dgTitle;
    }

    public void setDgTitle(String dgTitle) {
        this.dgTitle = dgTitle;
    }

    public Integer getDgType() {
        return dgType;
    }

    public void setDgType(Integer dgType) {
        this.dgType = dgType;
    }

    public Integer getDgSort() {
        return dgSort;
    }

    public void setDgSort(Integer dgSort) {
        this.dgSort = dgSort;
    }

    public ArrayList getIsUpdateImg() {
        return isUpdateImg;
    }

    public void setIsUpdateImg(ArrayList isUpdateImg) {
        this.isUpdateImg = isUpdateImg;
    }

    public MultipartFile getFileImgShow() {
        return fileImgShow;
    }

    public void setFileImgShow(MultipartFile fileImgShow) {
        this.fileImgShow = fileImgShow;
    }

    public String getDgDetail() {
        return dgDetail;
    }

    public void setDgDetail(String dgDetail) {
        this.dgDetail = dgDetail;
    }

    public Integer[] getDghId() {
        return dghId;
    }

    public void setDghId(Integer[] dghId) {
        this.dghId = dghId;
    }

    public String[] getDghName() {
        return dghName;
    }

    public void setDghName(String[] dghName) {
        this.dghName = dghName;
    }

    public Integer[] getDghCost() {
        return dghCost;
    }

    public void setDghCost(Integer[] dghCost) {
        this.dghCost = dghCost;
    }

    public Integer[] getDghSalesPrice() {
        return dghSalesPrice;
    }

    public void setDghSalesPrice(Integer[] dghSalesPrice) {
        this.dghSalesPrice = dghSalesPrice;
    }

    public Integer[] getDghActivityPrice() {
        return dghActivityPrice;
    }

    public void setDghActivityPrice(Integer[] dghActivityPrice) {
        this.dghActivityPrice = dghActivityPrice;
    }

    public Integer[] getDghFormulaId() {
        return dghFormulaId;
    }

    public void setDghFormulaId(Integer[] dghFormulaId) {
        this.dghFormulaId = dghFormulaId;
    }
}
