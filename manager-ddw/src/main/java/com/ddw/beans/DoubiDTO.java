package com.ddw.beans;

public class DoubiDTO {
    private Integer id;
    private String drName;
    private Integer drCost;
    private Integer drSort;
    private Integer drDoubiNum;
    private Integer drDiscountCode;
    private String drDesc;

    public String getDrDesc() {
        return drDesc;
    }

    public void setDrDesc(String drDesc) {
        this.drDesc = drDesc;
    }

    public Integer getDrDoubiNum() {
        return drDoubiNum;
    }

    public void setDrDoubiNum(Integer drDoubiNum) {
        this.drDoubiNum = drDoubiNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    public Integer getDrCost() {
        return drCost;
    }

    public void setDrCost(Integer drCost) {
        this.drCost = drCost;
    }


    public Integer getDrSort() {
        return drSort;
    }

    public void setDrSort(Integer drSort) {
        this.drSort = drSort;
    }

    public Integer getDrDiscountCode() {
        return drDiscountCode;
    }

    public void setDrDiscountCode(Integer drDiscountCode) {
        this.drDiscountCode = drDiscountCode;
    }
}
