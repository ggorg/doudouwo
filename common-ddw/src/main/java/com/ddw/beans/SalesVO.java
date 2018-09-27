package com.ddw.beans;

import java.io.Serializable;

public class SalesVO implements Serializable {

    private Integer busId;
    private Integer allSales;
    private Integer monthSales;

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getAllSales() {
        return allSales;
    }

    public void setAllSales(Integer allSales) {
        this.allSales = allSales;
    }

    public Integer getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(Integer monthSales) {
        this.monthSales = monthSales;
    }

    @Override
    public String toString() {
        return "SalesVO{" +
                "busId=" + busId +
                ", allSales=" + allSales +
                ", monthSales=" + monthSales +
                '}';
    }
}
