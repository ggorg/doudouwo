package com.ddw.beans;

public class StoreProductFormulaMaterialPO {

    private Integer materialId;
    private Integer weight;
    private Integer countNumber;
    private Integer countNetWeight;
    private String proDesc;
    private String mName;
    private Integer mNetWeight;
    private String dsUnit;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDsUnit() {
        return dsUnit;
    }

    public void setDsUnit(String dsUnit) {
        this.dsUnit = dsUnit;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(Integer countNumber) {
        this.countNumber = countNumber;
    }

    public Integer getCountNetWeight() {
        return countNetWeight;
    }

    public void setCountNetWeight(Integer countNetWeight) {
        this.countNetWeight = countNetWeight;
    }

    public String getProDesc() {
        return proDesc;
    }

    public void setProDesc(String proDesc) {
        this.proDesc = proDesc;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Integer getmNetWeight() {
        return mNetWeight;
    }

    public void setmNetWeight(Integer mNetWeight) {
        this.mNetWeight = mNetWeight;
    }
}
