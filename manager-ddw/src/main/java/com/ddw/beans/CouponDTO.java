package com.ddw.beans;

import java.math.BigDecimal;
import java.util.Date;

public class CouponDTO {
    private Integer id;
    private String dcName;
    private Integer dcType;
    private String dcMoney;
    private String dtActiveTime;
    private String dcDesc;
    private Integer dcMinPrice;
    private Integer storeProportion;

    public Integer getStoreProportion() {
        return storeProportion;
    }

    public void setStoreProportion(Integer storeProportion) {
        this.storeProportion = storeProportion;
    }

    public Integer getDcMinPrice() {
        return dcMinPrice;
    }

    public void setDcMinPrice(Integer dcMinPrice) {
        this.dcMinPrice = dcMinPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDcName() {
        return dcName;
    }

    public void setDcName(String dcName) {
        this.dcName = dcName;
    }

    public Integer getDcType() {
        return dcType;
    }

    public void setDcType(Integer dcType) {
        this.dcType = dcType;
    }

    public String getDcMoney() {
        return dcMoney;
    }

    public void setDcMoney(String dcMoney) {
        this.dcMoney = dcMoney;
    }

    public String getDtActiveTime() {
        return dtActiveTime;
    }

    public void setDtActiveTime(String dtActiveTime) {
        this.dtActiveTime = dtActiveTime;
    }

    public String getDcDesc() {
        return dcDesc;
    }

    public void setDcDesc(String dcDesc) {
        this.dcDesc = dcDesc;
    }
}
