package com.ddw.beans;

import java.util.Arrays;

public class StoreFormulaDTO {
    private Integer id;
    private String dfName;
    private String dfNumber;
    private Integer[] materialId;
    private Integer[] weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDfName() {
        return dfName;
    }

    public void setDfName(String dfName) {
        this.dfName = dfName;
    }

    public String getDfNumber() {
        return dfNumber;
    }

    public void setDfNumber(String dfNumber) {
        this.dfNumber = dfNumber;
    }

    public Integer[] getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer[] materialId) {
        this.materialId = materialId;
    }

    public Integer[] getWeight() {
        return weight;
    }

    public void setWeight(Integer[] weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "StoreFormulaDTO{" +
                "id=" + id +
                ", dfName='" + dfName + '\'' +
                ", dfNumber='" + dfNumber + '\'' +
                ", materialId=" + Arrays.toString(materialId) +
                ", weight=" + Arrays.toString(weight) +
                '}';
    }
}
