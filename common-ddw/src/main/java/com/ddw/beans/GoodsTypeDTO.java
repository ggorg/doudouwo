package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class GoodsTypeDTO {
    private Integer id;
    private String dgtName;
    private Integer dgtSort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDgtName() {
        return dgtName;
    }

    public void setDgtName(String dgtName) {
        this.dgtName = dgtName;
    }

    public Integer getDgtSort() {
        return dgtSort;
    }

    public void setDgtSort(Integer dgtSort) {
        this.dgtSort = dgtSort;
    }
}
