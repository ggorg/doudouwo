package com.ddw.beans;

import java.util.Date;

public class CouponPO {
    private Integer id;
    private String dcName;
    private Integer dcType;
    private Integer dcMoney;
    private Date dcStartTime;
    private Date dcEndTime;
    private String dcDesc;
    private Integer dcMinPrice;

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

    public Integer getDcMoney() {
        return dcMoney;
    }

    public void setDcMoney(Integer dcMoney) {
        this.dcMoney = dcMoney;
    }

    public Date getDcStartTime() {
        return dcStartTime;
    }

    public void setDcStartTime(Date dcStartTime) {
        this.dcStartTime = dcStartTime;
    }

    public Date getDcEndTime() {
        return dcEndTime;
    }

    public void setDcEndTime(Date dcEndTime) {
        this.dcEndTime = dcEndTime;
    }

    public String getDcDesc() {
        return dcDesc;
    }

    public void setDcDesc(String dcDesc) {
        this.dcDesc = dcDesc;
    }

    public Integer getDcMinPrice() {
        return dcMinPrice;
    }

    public void setDcMinPrice(Integer dcMinPrice) {
        this.dcMinPrice = dcMinPrice;
    }
}
